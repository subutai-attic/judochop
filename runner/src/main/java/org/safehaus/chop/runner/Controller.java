package org.safehaus.chop.runner;


import java.util.Set;

import org.reflections.Reflections;
import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.runner.drivers.Driver;
import org.safehaus.chop.runner.drivers.IterationDriver;
import org.safehaus.chop.runner.drivers.TimeDriver;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.annotations.IterationChop;
import org.safehaus.chop.api.annotations.TimeChop;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * The Controller controls the process of executing chops on test classes.
 */
@Singleton
public class Controller implements IController, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger( Controller.class );
    private final Object lock = new Object();

    private Set<Class<?>> timeChopClasses;
    private Set<Class<?>> iterationChopClasses;
    private State state = State.READY;
    private Driver<?> currentDriver;

    private StoreService service;
    private ProjectFig project;
    private int runNumber;


    @Inject
    private void setProject( ProjectFig project ) {
        LOG.info( "Controller injected with project properties: {}", project );

        if ( project.getLoadKey() == null ) {
            state = State.INACTIVE;
        }

        this.project = project;

        Reflections reflections = new Reflections( project.getTestPackageBase() );
        timeChopClasses = reflections.getTypesAnnotatedWith( TimeChop.class );
        iterationChopClasses = reflections.getTypesAnnotatedWith( IterationChop.class );

        if ( timeChopClasses.isEmpty() && iterationChopClasses.isEmpty() ) {
            state = State.INACTIVE;
        }
        else {
            state = State.READY;
        }
    }


    @Inject
    private void setStoreService( StoreService service ) {
        Preconditions.checkNotNull( service, "The StoreService cannot be null." );
        this.service = service;
    }


    @Override
    public StatsSnapshot getCurrentChopStats() {
        return currentDriver.getChopStats();
    }


    @Override
    public State getState() {
        return state;
    }


    @Override
    public boolean isRunning() {
        return state == State.RUNNING;
    }


    @Override
    public boolean needsReset() {
        return state == State.STOPPED;
    }


    @Override
    public ProjectFig getProject() {
        return project;
    }


    @Override
    public void reset() {
        synchronized ( lock ) {
            Preconditions.checkState( state.accepts( Signal.RESET, State.READY ), "Cannot reset the controller in state: " + state );
            state = state.next( Signal.RESET );
            currentDriver = null;
        }
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            Preconditions.checkState( state.accepts( Signal.START ), "Cannot start the controller in state: " + state );
            runNumber++;
            state = state.next( Signal.START );
            new Thread( this ).start();
            lock.notifyAll();
        }
    }


    @Override
    public void stop() {
        synchronized ( lock ) {
            Preconditions.checkState( state.accepts( Signal.STOP ), "Cannot stop a controller in state: " + state );
            state = state.next( Signal.STOP );
            lock.notifyAll();
        }
    }


    /*
     * @TODO
     *
     * There is a potential for the distributed runners to fall out of synchronization where
     * one runner may be running a different chop than the others because it is faster or
     * slower. There needs to be a way to synchronize across runners so they can run the same
     * chop together at the same time.
     *
     * Perhaps between chops we can make the runners stop and ask if they're ready to execute
     * the next chop. Then they can start executing together synchronously. This will require
     * an additional client call to determine if they're all ready to execute the same chop.
     *
     */

    @Override
    public void run() {
        for ( Class<?> iterationTest : iterationChopClasses ) {
            synchronized ( lock ) {
                currentDriver = new IterationDriver( iterationTest );
                currentDriver.setTimeout( project.getTestStopTimeout() );
                currentDriver.start();
                lock.notifyAll();
            }

            LOG.info( "Started new IterationDriver driver: controller state = {}", state );
            while ( currentDriver.blockTilDone( project.getTestStopTimeout() ) ) {
                if ( state == State.STOPPED ) {
                    LOG.info( "Got the signal to stop running." );
                    synchronized ( lock ) {
                        currentDriver.stop();
                        currentDriver = null;
                        lock.notifyAll();
                    }
                    return;
                }
            }

            LOG.info( "Out of while loop. controller state = {}, currentDriver is running = {}",
                    state, currentDriver.isRunning());

            if ( currentDriver.isComplete() ) {
                Summary summary = new Summary( runNumber );
                summary.setIterationTracker( ( ( IterationDriver ) currentDriver ).getTracker() );
                service.store( project, summary, currentDriver.getResultsFile() );
            }
        }

        for ( Class<?> timeTest : timeChopClasses ) {
            synchronized ( lock ) {
                currentDriver = new TimeDriver( timeTest );
                currentDriver.setTimeout( project.getTestStopTimeout() );
                currentDriver.start();
                lock.notifyAll();
            }

            LOG.info( "Started new TimeDriver driver: controller state = {}", state );
            while ( currentDriver.blockTilDone( project.getTestStopTimeout() ) ) {
                if ( state == State.STOPPED ) {
                    LOG.info( "Got the signal to stop running." );
                    synchronized ( lock ) {
                        currentDriver.stop();
                        currentDriver = null;
                        lock.notifyAll();
                    }
                    return;
                }
            }

            LOG.info( "Out of while loop. controller state = {}, currentDriver is running = {}",
                    state, currentDriver.isRunning());

            if ( currentDriver.isComplete() ) {
                Summary summary = new Summary( runNumber );
                summary.setTimeTracker( ( ( TimeDriver ) currentDriver ).getTracker() );
                service.store( project, summary, currentDriver.getResultsFile() );
            }
        }

        LOG.info( "The controller has completed." );
        currentDriver = null;
        state = state.next( Signal.COMPLETED );
    }
}
