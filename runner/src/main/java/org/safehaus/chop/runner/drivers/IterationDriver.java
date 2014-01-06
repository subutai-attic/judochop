package org.safehaus.chop.runner.drivers;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.IterationChop;


/**
 * Runs an iteration based unit test.
 */
public class IterationDriver extends Driver<IterationTracker> {
    private final CountDownLatch latch;

    public IterationDriver( Class<?> testClass ) {
        super( new IterationTracker( testClass ) );
        latch = new CountDownLatch( getTracker().getThreads() );
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = state.next( Signal.START );

                executorService.submit( new Runnable() {
                    @Override
                    public void run() {
                        LOG.info( "Started completion detection job." );

                        try {
                            while ( latch.getCount() > 0 ) {
                                latch.await( getTimeout(), TimeUnit.MILLISECONDS );
                            }
                        }
                        catch ( InterruptedException e ) {
                            LOG.warn( "Awe snap! Someone woke me up early!", e );
                        }

                        LOG.info( "All threads stopped processing. Time to stop tracker and complete." );
                        getTracker().stop();
                        state = state.next( Signal.COMPLETED );
                        lock.notifyAll();
                    }
                } );

                final IterationChop iterationChop = getTracker().getIterationChop();
                for ( int ii = 0; ii < getTracker().getThreads(); ii++ ) {
                    executorService.submit( new Runnable() {
                        @Override
                        public void run() {
                            for ( int ii = 0; ii < iterationChop.iterations() && isRunning(); ii++ ) {
                                LOG.info( "Starting {}-th iteration", ii );

                                // execute the tests and capture tracker
                                getTracker().execute();

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

                            latch.countDown();
                        }
                    } );
                }
            }
        }
    }
}
