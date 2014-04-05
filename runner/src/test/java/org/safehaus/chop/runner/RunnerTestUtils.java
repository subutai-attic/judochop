package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.runner.rest.StatsResource;
import org.safehaus.jettyjam.utils.TestParams;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;


/**
 * Common tests run in unit and test mode.
 */
public class RunnerTestUtils {

    public static void testStart( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( Runner.START_POST )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), Runner.START_POST );
    }


    public static void testReset( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( Runner.RESET_POST )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), Runner.RESET_POST );
    }


    public static void testStop( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( Runner.STOP_POST )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), Runner.STOP_POST );
    }


    public static void testStats( final TestParams testParams ) {
        StatsSnapshot snapshot = testParams
                .setEndpoint( StatsResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .get( StatsSnapshot.class );

        assertNotNull( snapshot );
    }


    public static void testStatus( final TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( Runner.STATUS_GET )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .get( BaseResult.class );

        assertEquals( result.getEndpoint(), Runner.STATUS_GET );
    }
}
