package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.runner.rest.ResetResource;
import org.safehaus.chop.runner.rest.StartResource;
import org.safehaus.jettyjam.utils.TestParams;

import static junit.framework.TestCase.assertEquals;


/**
 * Common tests run in unit and test mode.
 */
public class RunnerTestUtils {

    public static void testStart( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( StartResource.ENDPOINT_URL )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), StartResource.ENDPOINT_URL );
        assertEquals( StartResource.TEST_MESSAGE, result.getMessage() );
    }


    public static void testReset( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( ResetResource.ENDPOINT_URL )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );
        assertEquals( result.getEndpoint(), ResetResource.ENDPOINT_URL );
    }
}
