package org.safehaus.perftest;

import com.google.inject.Guice;

import org.junit.Ignore;
import org.junit.Test;

import org.safehaus.perftest.api.store.amazon.AmazonStoreModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 */
public class PerftestRunnerTest {
    @Test @Ignore( "Need to rewrite this test now that we start in INACTIVE mode" )
    public void testPerftestRunner() throws InterruptedException {
        PerftestRunner runner = Guice.createInjector( new PerftestModule() ).getInstance( PerftestRunner.class );
        assertFalse( runner.isRunning() );

        runner.start();
        assertTrue( runner.isRunning() );

        while ( runner.isRunning() )
        {
            Thread.sleep( 100 );
        }

        runner.stop();
        assertFalse( runner.isRunning() );

        assertEquals( 1000, runner.getCallStatsSnapshot().getCallCount() );
    }
}
