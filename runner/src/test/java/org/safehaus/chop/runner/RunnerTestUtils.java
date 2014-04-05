package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.client.rest.RestRequests;
import org.safehaus.jettyjam.utils.TestParams;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;


/**
 * Common tests run in unit and test mode.
 */
public class RunnerTestUtils {

    public static void testStart( TestParams testParams ) {
        Result result = RestRequests.newStartOp(
                testParams.setEndpoint( Runner.START_POST ).newWebResource() ).execute( Result.class );

        assertEquals( result.getEndpoint(), Runner.START_POST );
    }


    public static void testReset( TestParams testParams ) {
        Result result = RestRequests.newResetOp(
                testParams.setEndpoint( Runner.RESET_POST ).newWebResource() ).execute( Result.class );

        assertEquals( result.getEndpoint(), Runner.RESET_POST );
    }


    public static void testStop( TestParams testParams ) {
        Result result = RestRequests.newStopOp(
                testParams.setEndpoint( Runner.STOP_POST ).newWebResource() ).execute( Result.class );

        assertEquals( result.getEndpoint(), Runner.STOP_POST );
    }


    public static void testStats( final TestParams testParams ) {
        StatsSnapshot snapshot = RestRequests.newStatsOp(
                testParams.setEndpoint( Runner.STATS_GET ).newWebResource() ).execute( StatsSnapshot.class );

        assertNotNull( snapshot );
    }


    public static void testStatus( final TestParams testParams ) {
        Result result = RestRequests.newStatusOp(
                testParams.setEndpoint( Runner.STATUS_GET ).newWebResource() ).execute( Result.class );

        assertEquals( result.getEndpoint(), Runner.STATUS_GET );
    }
}
