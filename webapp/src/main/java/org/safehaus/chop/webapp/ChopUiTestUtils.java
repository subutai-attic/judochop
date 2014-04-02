package org.safehaus.chop.webapp;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.webapp.coordinator.rest.ResetResource;
import org.safehaus.chop.webapp.coordinator.rest.RunManagerResource;
import org.safehaus.chop.webapp.coordinator.rest.StartResource;
import org.safehaus.chop.webapp.coordinator.rest.StopResource;
import org.safehaus.jettyjam.utils.TestParams;

import static junit.framework.TestCase.assertEquals;


/**
 * Common tests run in unit and test mode.
 */
public class ChopUiTestUtils {

    public static void testRunManagerNext( TestParams testParams ) {
        Integer next = testParams
                .setEndpoint( RunManagerResource.ENDPOINT )
                .newWebResource()
                .path( "/next" )
                .accept( MediaType.APPLICATION_JSON )
                .get( Integer.class );

        assertEquals( 0, next.intValue() );
    }

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
}
