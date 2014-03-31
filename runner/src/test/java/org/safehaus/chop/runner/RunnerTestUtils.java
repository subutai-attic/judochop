package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.runner.rest.ResetResource;
import org.safehaus.chop.runner.rest.StartResource;
import org.safehaus.chop.runner.rest.StatsResource;
import org.safehaus.chop.runner.rest.StatusResource;
import org.safehaus.chop.runner.rest.StopResource;
import org.safehaus.jettyjam.utils.TestParams;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;


/**
 * Common tests run in unit and test mode.
 */
public class RunnerTestUtils {

    public static void testStart( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( StartResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), StartResource.ENDPOINT );
    }


    public static void testReset( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( ResetResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), ResetResource.ENDPOINT );
    }


    public static void testStop( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( StopResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), StopResource.ENDPOINT );
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
                .setEndpoint( StatusResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .get( BaseResult.class );

        assertEquals( result.getEndpoint(), StatusResource.ENDPOINT );
    }
}
