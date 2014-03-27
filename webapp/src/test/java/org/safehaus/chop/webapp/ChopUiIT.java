package org.safehaus.chop.webapp;


import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.webapp.coordinator.rest.TestGetResource;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.jettyjam.utils.CertUtils;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyJarResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

import static junit.framework.TestCase.assertEquals;


/**
 * An integration test for the chop UI.
 */
public class ChopUiIT {
    private final static Logger LOG = LoggerFactory.getLogger( ChopUiIT.class );

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
    public static JettyJarResource jetty = new JettyJarResource();

    static {
        CertUtils.preparations( jetty.getHostname(), jetty.getPort() );
    }

    @Test
    public void testGet() {
        TestData testData = new TestData( jetty ).setLogger( LOG ).setEndpoint( TestGetResource.ENDPOINT_URL );
        String result = TestUtils.testGet( testData );
        assertEquals( TestGetResource.TEST_MESSAGE, result );
    }


    @Test
    public void testUpload() {
        TestData testData = new TestData( jetty ).setLogger( LOG ).setEndpoint( UploadResource.ENDPOINT_URL );
        String result = TestUtils.testUpload( testData );
        LOG.debug( "Got back result = {}", result );
    }
}
