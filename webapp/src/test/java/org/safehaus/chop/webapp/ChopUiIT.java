package org.safehaus.chop.webapp;


import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.webapp.coordinator.rest.TestGetResource;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyIntegResource;

import org.safehaus.jettyjam.utils.JettyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

import static junit.framework.TestCase.assertEquals;


/**
 * An integration test for the chop UI.
 */
public class ChopUiIT {
    private final static Logger LOG = LoggerFactory.getLogger( ChopUiIT.class );

    private final static String[] args = new String[] { "-e" };

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
    public static JettyResource jetty = new JettyIntegResource( args );


    @Test
    public void testGet() {
        String result = jetty.newTestParams()
                             .setEndpoint( TestGetResource.ENDPOINT_URL )
                             .newWebResource()
                             .accept( MediaType.TEXT_PLAIN )
                             .get( String.class );
        assertEquals( TestGetResource.TEST_MESSAGE, result );
    }


    @Test
    public void testRunManagerNext() {
        ChopUiTestUtils.testRunManagerNext( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryList() {
        ChopUiTestUtils.testRunnerRegistryList( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryRegister() {
        ChopUiTestUtils.testRunnerRegistryRegister( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryUnregister() {
        ChopUiTestUtils.testRunnerRegistryUnregister( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistrySequence() {
        ChopUiTestUtils.testRunnerRegistrySequence( jetty.newTestParams().setLogger( LOG ) );
    }
}
