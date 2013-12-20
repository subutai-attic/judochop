package org.safehaus.chop.server.runners;


import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.annotations.TimeChop;


/**
 * Runs a time based unit test.
 */
public class TimeRunner extends Runner<TimeTracker> {

    public TimeRunner( Class<?> testClass ) {
        super( new TimeTracker( testClass ) );
    }


    @Override
    public void start() {
        synchronized ( lock ) {
            if ( state == State.READY ) {
                state = State.RUNNING;
                tracker.reset();
                tracker.setStartTime( System.currentTimeMillis() );

                final TimeChop timeChop = tracker.getTimeChop();
                for ( int ii = 0; ii < tracker.getThreads(); ii++ ) {
                    executorService.submit( new Runnable() {
                        @Override
                        public void run() {
                            long runTime;

                            do {
                                runTime = System.currentTimeMillis() - tracker.getStartTime();
                                LOG.info( "Running for {} ms, will stop in {} ms", runTime, timeChop.time() - runTime );

                                // execute the tests and capture tracker
                                tracker.execute();

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

                lock.notifyAll();
            }
        }
    }
}
