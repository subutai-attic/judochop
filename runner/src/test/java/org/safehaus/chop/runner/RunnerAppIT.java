package org.safehaus.chop.runner;


import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.runner.rest.StartResource;
import org.safehaus.jettyjam.utils.CertUtils;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyJarResource;

import org.safehaus.jettyjam.utils.JettyRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import static junit.framework.TestCase.assertEquals;


/**
 * An integration test for the chop UI.
 */
public class RunnerAppIT {
    private final static Logger LOG = LoggerFactory.getLogger( RunnerAppIT.class );
    private final static Properties systemProperties = new Properties();

    static {
        systemProperties.setProperty( RunnerConfig.CHOP_IT_MODE, "true" );
    }

    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = RunnerConfig.class ) },
        filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = Runner.DEFAULT_SERVER_PORT_INT ) }
    )
    @ClassRule
    public static JettyJarResource jetty = new JettyJarResource();


    @Test
    public void testStart() {
        LOG.info( "test start ..." );
        CertUtils.preparations( jetty.getHostname(), jetty.getPort() );
        String serverUrl = jetty.getAppProperties().getProperty( JettyRunner.SERVER_URL );
        WebResource resource = Client.create().resource( serverUrl + StartResource.ENDPOINT_URL );
        String result = resource.queryParam( StartResource.TEST_PARAM, Boolean.TRUE.toString() )
                                .type( MediaType.TEXT_PLAIN_TYPE ).get( String.class );
        assertEquals( StartResource.TEST_MESSAGE, result );
    }
}
