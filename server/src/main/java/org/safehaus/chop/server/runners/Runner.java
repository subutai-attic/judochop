package org.safehaus.chop.server.runners;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.safehaus.perftest.api.CallStatsSnapshot;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An abstract chop runner.
 */
public abstract class Runner<T extends Tracker> implements IRunner {
    public static final long TIMEOUT = 1000L;
    protected static final Logger LOG = LoggerFactory.getLogger( Runner.class );
    protected final T tracker;
    protected final Object lock = new Object();
    protected ExecutorService executorService;
    protected State state = State.READY;
    protected long timeout = TIMEOUT; // @TODO should be configurable


    public Runner( T tracker ) {
        this.tracker = tracker;
        executorService = Executors.newFixedThreadPool( tracker.getThreads() );
    }


    // @TODO should be configurable
    public void setTimeout( long timeout ) {
        this.timeout = timeout;
    }


    @Override
    public void reset() {
        synchronized ( lock ) {
            if ( state == State.STOPPED ) {
                state = State.READY;
                tracker.reset();
                executorService = Executors.newFixedThreadPool( tracker.getThreads() );
            }
        }
    }


    @Override
    public CallStatsSnapshot getCallStatsSnapshot() {
        return null;
    }


    @Override
    // @TODO do not expose state to the outside (use isRunning() etc)
    public State getState() {
        return state;
    }


    @Override
    public RunInfo getRunInfo() {
        return null;
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
        return tracker.getStartTime();
    }


    @Override
    public long getStopTime() {
        return tracker.getStopTime();
    }


    @Override
    public TestInfo getTestInfo() {
        return null;
    }


    /**
     * Blocks until the runner is done or until the timeout amount. Returns true if
     * we need to keep blocking and this runner is not done yet.
     */
    public boolean blockTilDone( long timeout ) throws InterruptedException {
        synchronized ( lock ) {
            if ( state == State.RUNNING ) {
                lock.wait( timeout );
            }

            return state == State.RUNNING;
        }
    }


    @Override
    public void stop() {
        synchronized ( lock ) {
            if ( state == State.RUNNING ) {
                state = State.STOPPED;

                executorService.shutdown();
                try {
                    if ( ! executorService.awaitTermination( timeout, TimeUnit.MILLISECONDS ) ) {
                        executorService.shutdownNow();
                    }
                }
                catch ( InterruptedException e ) {
                    LOG.warn( "Awe snap! Someone woke me up early!" );
                }

                lock.notifyAll();
            }
        }
    }
}
