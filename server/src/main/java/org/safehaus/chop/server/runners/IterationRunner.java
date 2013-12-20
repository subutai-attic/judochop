package org.safehaus.chop.server.runners;


import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.annotations.IterationChop;


/**
 * Runs an iteration based unit test.
 */
public class IterationRunner extends Runner<IterationTracker> {

    public IterationRunner( Class<?> testClass ) {
        super( new IterationTracker( testClass ) );
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = State.RUNNING;
                tracker.reset();
                tracker.setStartTime( System.currentTimeMillis() );

                final IterationChop iterationChop = tracker.getIterationChop();
                for ( int ii = 0; ii < tracker.getThreads(); ii++ ) {
                    executorService.submit( new Runnable() {
                        @Override
                        public void run() {
                        for ( int ii = 0; ii < iterationChop.iterations() && isRunning(); ii++ ) {
                            LOG.info( "Starting {}-th iteration", ii );

                            // execute the tests and capture tracker
                            tracker.execute();

                            // if a delay between runs is requested apply it
                            if ( iterationChop.delay() > 0 ) {
                                try {
                                    Thread.sleep( iterationChop.delay() );
                                }
                                catch ( InterruptedException e ) {
                                    LOG.warn( "Awe snap, someone woke me up early!" );
                                }
                            }
                        }
                        }
                    } );
                }

                lock.notifyAll();
            }
        }
    }
}
