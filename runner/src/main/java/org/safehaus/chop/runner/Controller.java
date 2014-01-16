package org.safehaus.chop.runner;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.runner.drivers.Driver;
import org.safehaus.chop.runner.drivers.IterationDriver;
import org.safehaus.chop.runner.drivers.TimeDriver;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.IterationChop;
import org.safehaus.chop.api.TimeChop;
import org.safehaus.chop.api.StoreService;
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

    // @todo make this configurable and also put this into the project or runner fig
    private static final long DEFAULT_LAGER_WAIT_TIMEOUT_MILLIS = 120000;
    private final Object lock = new Object();

    private Set<Class<?>> timeChopClasses;
    private Set<Class<?>> iterationChopClasses;
    private State state = State.INACTIVE;
    private Driver<?> currentDriver;

    private Map<String, RunnerFig> otherRunners;
    private StoreService service;
    private Project project;
    private int runNumber;


    @Inject
    Controller( Project project, StoreService service, RunnerFig me ) {
        setProject( project );
        setStoreService( service );

        if ( state != State.INACTIVE ) {
            /*
             * The call to getNextRunNumber will return a number greater than or
             * equal to 1. This is the next run number that should be used, however
             * the runNumber is also incremented every time start is called. So
             * we need to decrement this value by 1 in order not to skip run numbers.
             */
            runNumber = service.getNextRunNumber( me, project ) - 1;
            otherRunners = service.getRunners( me );
        }
    }


    private void setProject( Project project ) {
        // if the project is null which should never really happen we just return
        // and stay in the INACTIVE state waiting for a load to activate this runner
        if ( project == null ) {
            return;
        }

        // setup the valid runner project
        this.project = project;
        LOG.info( "Controller injected with project properties: {}", project );

        // if the project test package base is null there's nothing we can do but
        // return and stay in the inactive state waiting for a load to occur
        if ( project.getTestPackageBase() == null ) {
            return;
        }

        // reflect into package base looking for annotated classes
        Reflections reflections = new Reflections( project.getTestPackageBase() );

        timeChopClasses = reflections.getTypesAnnotatedWith( TimeChop.class );
        LOG.info( "TimeChop classes = {}", timeChopClasses );

        iterationChopClasses = reflections.getTypesAnnotatedWith( IterationChop.class );
        LOG.info( "IterationChop classes = {}", iterationChopClasses );

        // if we don't have a valid project load key then this is bogus
        if ( project.getLoadKey() == null ) {
            state = State.INACTIVE;
            LOG.info( "Null loadKey: controller going into INACTIVE state." );
            return;
        }


        if ( timeChopClasses.isEmpty() && iterationChopClasses.isEmpty() ) {
            state = State.INACTIVE;
            LOG.info( "Nothing to scan: controller going into INACTIVE state." );
            return;
        }

        state = State.READY;
        LOG.info( "We have things to scan and a valid loadKey: controller going into READY state." );
    }


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
    public Project getProject() {
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


    /**
     * Gets the collection of runners that are still executing a chop on a test class.
     *
     * @param runNumber the current run number
     * @param testClass the current chop test
     * @return the runners still executing a test class
     */
    private Collection<RunnerFig> getLagers( int runNumber, Class<?> testClass ) {
        Collection<RunnerFig> lagers = new ArrayList<RunnerFig>( otherRunners.size() );

        for ( String runnerKey : otherRunners.keySet() ) {
            RunnerFig runner = otherRunners.get( runnerKey );
            if ( service.hasCompleted( runner, project, runNumber, testClass ) ) {
                LOG.info( "Runner {} has completed test {}", runner.getHostname(), testClass.getName() );
            }
            else {
                LOG.warn( "Waiting on runner {} to complete test {}", runner.getHostname(), testClass.getName() );
                lagers.add( runner );
            }
        }

        return lagers;
    }


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
                service.store( project, summary, currentDriver.getResultsFile(),
                        currentDriver.getTracker().getTestClass() );

                long startWaitingForLagers = System.currentTimeMillis();
                while ( state == State.RUNNING ) {
                    Collection<RunnerFig> lagers = getLagers( runNumber, iterationTest );
                    if ( lagers.size() > 0 ) {
                        LOG.info( "IterationChop test {} completed but waiting on lagging runners:\n{}",
                                iterationTest.getName(), lagers );
                    }
                    else {
                        LOG.info( "IterationChop test {} completed and there are NO lagging runners.",
                                iterationTest.getName() );
                        break;
                    }

                    synchronized ( lock ) {
                        try {
                            lock.wait( project.getTestStopTimeout() );
                        }
                        catch ( InterruptedException e ) {
                            LOG.error( "Awe snap! Someone woke me up before it was time!" );
                        }
                    }

                    boolean waitTimeoutReached = ( System.currentTimeMillis() - startWaitingForLagers )
                            > DEFAULT_LAGER_WAIT_TIMEOUT_MILLIS;

                    if ( waitTimeoutReached && ( lagers.size() > 0 ) ) {
                        LOG.warn( "Timeout reached. Not waiting anymore for lagers: {}", lagers );
                        break;
                    }
                }
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
                service.store( project, summary, currentDriver.getResultsFile(),
                        currentDriver.getTracker().getTestClass() );

                long startWaitingForLagers = System.currentTimeMillis();
                while ( state == State.RUNNING ) {
                    Collection<RunnerFig> lagers = getLagers( runNumber, timeTest );
                    if ( lagers.size() > 0 ) {
                        LOG.warn( "TimeChop test {} completed but waiting on lagging runners:\n{}",
                                timeTest.getName(), lagers );
                    }
                    else {
                        LOG.info( "TimeChop test {} completed and there are NO lagging runners.",
                                timeTest.getName() );
                        break;
                    }

                    synchronized ( lock ) {
                        try {
                            lock.wait( project.getTestStopTimeout() );
                        }
                        catch ( InterruptedException e ) {
                            LOG.error( "Awe snap! Someone woke me up before it was time!" );
                        }
                    }

                    boolean waitTimeoutReached = ( System.currentTimeMillis() - startWaitingForLagers )
                            > DEFAULT_LAGER_WAIT_TIMEOUT_MILLIS;

                    if ( waitTimeoutReached && ( lagers.size() > 0 ) ) {
                        LOG.warn( "Timeout reached. Not waiting anymore for lagers: {}", lagers );
                        break;
                    }
                }
            }
        }

        LOG.info( "The controller has completed." );
        currentDriver = null;
        state = state.next( Signal.COMPLETED );
    }
}
