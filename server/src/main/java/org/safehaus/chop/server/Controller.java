package org.safehaus.chop.server;


import java.util.Set;

import org.reflections.Reflections;
import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.server.drivers.Driver;
import org.safehaus.chop.server.drivers.IterationDriver;
import org.safehaus.chop.server.drivers.Stats;
import org.safehaus.chop.server.drivers.TimeDriver;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.annotations.IterationChop;
import org.safehaus.chop.api.annotations.TimeChop;
import org.safehaus.chop.api.ConfigKeys;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


/**
 * The Controller controls the process of executing JChop tests.
 */
@Singleton
public class Controller implements IController, Runnable {
    public static final long TIMEOUT = 1000L;

    private static final Logger LOG = LoggerFactory.getLogger( Controller.class );
    private final Object lock = new Object();

    private Set<Class<?>> timeChopClasses;
    private Set<Class<?>> iterationChopClasses;
    private State state = State.READY;
    private long timeout = TIMEOUT; // @TODO should be configurable
    private Driver<?> currentRunner;

    private StoreService service;
    private Stats stats;
    private Project project;
    private ISummary summary = new Summary( 0 );

    private long startTime;
    private long stopTime;


    @Inject
    private void setCallStats( Stats stats ) {
        this.stats = stats;
    }

    @Inject
    private void setProject( Project project ) {
        if ( project == null ) {
            state = State.INACTIVE;
        }

        this.project = project;
    }


    @Inject
    private void setStoreService( StoreService service ) {
        this.service = service;
    }


    @Inject
    private void setBasePackage( @Named( ConfigKeys.TEST_PACKAGE_BASE ) String basePackage ) {
        Preconditions.checkNotNull( basePackage, "The basePackage cannot be null!" );
        Preconditions.checkArgument( basePackage.length() == 0,
                "The basePackage cannot be the empty string and must be a valid package." );

        Reflections reflections = new Reflections( basePackage );
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
    private void setTimeout( @Named( ConfigKeys.TEST_STOP_TIMEOUT ) long timeout ) {
        if ( timeout <= 0 ) {
            this.timeout = TIMEOUT;
        }
        else {
            this.timeout = timeout;
        }
    }


    @Override
    public void reset() {
        synchronized ( lock ) {
            if ( state == State.STOPPED ) {
                state = State.READY;
                summary = new Summary( summary.getRunNumber() + 1 );
                startTime = -1;
                stopTime = -1;
                currentRunner = null;
                stats.stop();
                stats.reset();
            }
        }
    }


    @Override
    public StatsSnapshot getCallStatsSnapshot() {
        return stats.getStatsSnapshot( isRunning(), getStartTime(), getStopTime() );
    }


    @Override
    // @TODO do not expose state to the outside (use isRunning() etc)
    public State getState() {
        return state;
    }


    @Override
    public ISummary getSummary() {
        return summary;
    }


    @Override
    public boolean isRunning() {
        synchronized ( lock ) {
            return state == State.RUNNING;
        }
    }


    @Override
    public boolean needsReset() {
        synchronized ( lock ) {
            return state == State.STOPPED;
        }
    }


    @Override
    public long getStartTime() {
        return startTime;
    }


    @Override
    public long getStopTime() {
        return stopTime;
    }


    @Override
    public Project getProject() {
        return project;
    }


    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = State.RUNNING;
                startTime = System.currentTimeMillis();
                new Thread( this ).start();
                lock.notifyAll();
            }
        }
    }


    @Override
    public void stop() {
        synchronized ( lock ) {
            if ( state == State.RUNNING ) {
                currentRunner.stop();
                if ( currentRunner.getState() != State.STOPPED ) {
                    LOG.warn( "Could not properly stop the current runner!" );
                }

                state = State.STOPPED;
                currentRunner = null;
                stopTime = System.currentTimeMillis();
                lock.notifyAll();
            }
        }
    }


    @Override
    public void run() {
        for ( Class<?> iterationTest : iterationChopClasses ) {
            synchronized ( lock ) {
                currentRunner = new IterationDriver( iterationTest );
                currentRunner.setTimeout( timeout );
                currentRunner.start();
                lock.notifyAll();
            }

            try {
                synchronized ( lock ) {
                    while ( currentRunner.blockTilDone( timeout ) ) {
                        if ( state == State.STOPPED ) {
                            currentRunner.stop();
                        }

                        lock.notifyAll();
                    }
                }
            }
            catch ( InterruptedException e ) {
                LOG.warn( "Awe snap! Someone woke me up early!" );
            }
        }

        for ( Class<?> timeTest : timeChopClasses ) {
            synchronized ( lock ) {
                currentRunner = new TimeDriver( timeTest );
                currentRunner.setTimeout( timeout );
                currentRunner.start();
                lock.notifyAll();
            }

            try {
                synchronized ( lock ) {
                    while ( currentRunner.blockTilDone( timeout ) ) {
                        if ( state == State.STOPPED ) {
                            currentRunner.stop();
                        }

                        lock.notifyAll();
                    }
                }
            }
            catch ( InterruptedException e ) {
                LOG.warn( "Awe snap! Someone woke me up early!" );
            }
        }

        LOG.info( "Test has stopped." );
        stopTime = System.currentTimeMillis();
        stats.stop();
        service.uploadResults( project, summary, stats.getResultsFile() );
        reset();
    }
}
