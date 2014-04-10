package org.apache.usergrid.chop.webapp;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.api.RestParams;
import org.apache.usergrid.chop.webapp.coordinator.rest.TestGetResource;
import org.apache.usergrid.chop.webapp.elasticsearch.ElasticSearchResource;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyResource;

import org.safehaus.jettyjam.utils.JettyUnitResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests the ChopUi.
 */
public class ChopUiTest {
    private static final Logger LOG = LoggerFactory.getLogger( ChopUiTest.class );
    private static final Map<String,String> queryParams = new HashMap<String, String>();


    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = ChopUiConfig.class ) },
        filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = 8443 ) }
    )
    @ClassRule
    public static JettyResource jetty = new JettyUnitResource( ChopUiTest.class );

    @ClassRule
    public static ElasticSearchResource es = new ElasticSearchResource();

    static {
        queryParams.put( RestParams.PASSWORD, "pass" );
        queryParams.put( RestParams.USERNAME, "user" );
        queryParams.put( RestParams.COMMIT_ID, UUID.randomUUID().toString() );
        queryParams.put( RestParams.MODULE_VERSION, "2.0.0-SNAPSHOT" );
        queryParams.put( RestParams.MODULE_ARTIFACTID, "chop-example" );
        queryParams.put( RestParams.MODULE_GROUPID, "org.safehaus.chop" );
        queryParams.put( RestParams.TEST_PACKAGE, "org.safehaus.chop.example" );
    }

    @Test
    public void testGet() {
        String result = jetty.newTestParams()
                .setLogger( LOG )
                .setEndpoint( TestGetResource.ENDPOINT_URL )
                .newWebResource()
                .accept( MediaType.TEXT_PLAIN )
                .get( String.class );
        assertEquals( TestGetResource.TEST_MESSAGE, result );
    }


    @Test
    public void testRunManagerNext() {
        ChopUiTestUtils.testRunManagerNext(jetty.newTestParams(queryParams).setLogger(LOG));
    }


    @Test
    public void testRunnerRegistryList() {
        ChopUiTestUtils.testRunnerRegistryList( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryRegister() {
        ChopUiTestUtils.testRunnerRegistryRegister( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testUploadRunner() throws Exception {
        ChopUiTestUtils.testUpload( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryUnregister() {
        ChopUiTestUtils.testRunnerRegistryUnregister( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistrySequence() {
        ChopUiTestUtils.testRunnerRegistrySequence( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }
}
