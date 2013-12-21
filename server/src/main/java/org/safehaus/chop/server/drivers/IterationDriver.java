package org.safehaus.chop.server.drivers;


import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.annotations.IterationChop;


/**
 * Runs an iteration based unit test.
 */
public class IterationDriver extends Driver<IterationTracker> {

    public IterationDriver( Class<?> testClass ) {
        super( new IterationTracker( testClass ) );
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = state.next( Signal.START );

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
                        }
                    } );
                }

                getTracker().stop();
                state = state.next( Signal.COMPLETED );
                lock.notifyAll();
            }
        }
    }
}
