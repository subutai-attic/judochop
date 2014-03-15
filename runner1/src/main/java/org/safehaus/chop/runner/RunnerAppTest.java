package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.safehaus.embedded.jetty.utils.HttpConnector;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.JettyResource;
import org.safehaus.embedded.jetty.utils.ServletMapping;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests the Hello World Servlet in embedded mode.
 */
public class RunnerAppTest {

    @JettyContext(
        servletMappings = {
                @ServletMapping( servlet = HelloWorldServlet.class, spec = "/*" )
        }
    )
    @JettyConnectors(
        defaultId = "https",
        httpConnectors = { @HttpConnector( id = "http" ) },
        httpsConnectors = { @HttpsConnector( id = "https" ) }
    )
    @Rule
    public JettyResource service = new JettyResource();


    @Test
    public void testHelloWorld() {
        CertUtils.preparations( service.getHostname(), service.getPort() );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        String result = client
                .resource( service.getServerUrl().toString() )
                .path( "/" )
                .accept( MediaType.TEXT_PLAIN )
                .get( String.class );

        assertEquals( HelloWorldServlet.MESSAGE, result );
    }
}
