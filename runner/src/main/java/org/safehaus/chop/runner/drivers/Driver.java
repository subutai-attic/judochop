package org.safehaus.chop.runner.drivers;


import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.StatsSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.netflix.config.DynamicLongProperty;


/**
 * An abstract chop runner.
 */
public abstract class Driver<T extends Tracker> implements IDriver<T> {
    protected static final Logger LOG = LoggerFactory.getLogger( Driver.class );
    private final T tracker;
    protected final Object lock = new Object();
    protected ExecutorService executorService;
    protected State state = State.READY;
    private DynamicLongProperty timeout;


    protected Driver( T tracker ) {
        this.tracker = tracker;

        // 1 extra for stopper thread and 1 extra for shits and giggles
        executorService = Executors.newFixedThreadPool( tracker.getThreads() + 2 );
    }


    public void setTimeout( DynamicLongProperty timeout ) {
        this.timeout = timeout;
    }


    public long getTimeout() {
        return this.timeout.get();
    }


    @Override
    public T getTracker() {
        return tracker;
    }


    @Override
    public StatsSnapshot getChopStats() {
        return new StatsSnapshot(
                tracker.getActualIterations(),
                tracker.getMaxTime(),
                tracker.getMinTime(),
                tracker.getMeanTime(),
                isRunning(),
                tracker.getStartTime()
        );
    }


    @Override
    public File getResultsFile() {
        Preconditions.checkState( ! isRunning(), "Cannot provide complete results while still running." );
        return tracker.getResultsFile();
    }


    @Override
    public boolean isComplete() {
        synchronized ( lock ) {
            return state == State.COMPLETED;
        }
    }


    @Override
    public boolean isRunning() {
        synchronized ( lock ) {
            return state == State.RUNNING;
        }
    }


    @Override
    public boolean isStopped() {
        synchronized ( lock ) {
            return state == State.STOPPED;
        }
    }


    /**
     * Blocks until the runner is done or until the timeout amount. Returns true if
     * we need to keep blocking and this runner is not done yet.
     */
    public boolean blockTilDone( long timeout ) {
        Preconditions.checkState( isRunning() || isComplete(), "Cannot block on a driver that is not running: state = " + state );

        synchronized ( lock ) {
            if ( state == State.RUNNING ) {
                try {
                    lock.wait( timeout );
                }
                catch ( InterruptedException e ) {
                    LOG.warn( "Awe snap, someone woke me up early!" );
                }
            }

            return state == State.RUNNING;
        }
    }


    @Override
    public void stop() {
        Preconditions.checkState( isRunning(), "Cannot stop driver that is not running." );

        synchronized ( lock ) {
            if ( state == State.RUNNING ) {
                state = state.next( Signal.STOP );

                executorService.shutdown();
                try {
                    if ( ! executorService.awaitTermination( timeout.get(), TimeUnit.MILLISECONDS ) ) {
                        executorService.shutdownNow();
                    }
                }
                catch ( InterruptedException e ) {
                    LOG.warn( "Awe snap! Someone woke me up early!" );
                }
                finally {
                    tracker.stop();
                    lock.notifyAll();
                }
            }
        }
    }
}
