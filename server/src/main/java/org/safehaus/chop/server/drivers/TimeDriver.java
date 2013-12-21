package org.safehaus.chop.server.drivers;


import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.annotations.TimeChop;


/**
 * Runs a time based unit test.
 */
public class TimeDriver extends Driver<TimeTracker> {

    public TimeDriver( Class<?> testClass ) {
        super( new TimeTracker( testClass ) );
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = state.next( Signal.START );

                final TimeChop timeChop = getTracker().getTimeChop();
                for ( int ii = 0; ii < getTracker().getThreads(); ii++ ) {
                    executorService.submit( new Runnable() {
                        @Override
                        public void run() {
                            long runTime;

                            do {
                                runTime = System.currentTimeMillis() - getTracker().getStartTime();
                                LOG.info( "Running for {} ms, will stop in {} ms", runTime, timeChop.time() - runTime );

                                // execute the tests and capture tracker
                                getTracker().execute();

                                // if a delay between runs is requested apply it
                                if ( timeChop.delay() > 0 ) {
                                    try {
                                        Thread.sleep( timeChop.delay() );
                                    }
                                    catch ( InterruptedException e ) {
                                        LOG.warn( "Awe snap, someone woke me up early!" );
                                    }
                                }
                            }
                            while ( runTime < timeChop.time() && isRunning() );
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
