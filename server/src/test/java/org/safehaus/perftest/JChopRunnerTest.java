package org.safehaus.perftest;


import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 */
public class JChopRunnerTest {
    @Test
    @Ignore("Need to rewrite this test now that we start in INACTIVE mode")
    public void testPerftestRunner() throws InterruptedException {
        JChopRunner runner = Guice.createInjector( new JChopModule() ).getInstance( LegacyRunner.class );
        assertFalse( runner.isRunning() );

        runner.start();
        assertTrue( runner.isRunning() );

        while ( runner.isRunning() ) {
            Thread.sleep( 100 );
        }

        runner.stop();
        assertFalse( runner.isRunning() );

        assertEquals( 1000, runner.getCallStatsSnapshot().getTestClassRuns() );
    }
}
