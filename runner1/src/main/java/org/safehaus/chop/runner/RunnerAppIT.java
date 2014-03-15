package org.safehaus.chop.runner;


import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.safehaus.embedded.jetty.utils.JettyJarResource;
import org.safehaus.embedded.jetty.utils.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static junit.framework.TestCase.assertEquals;


/**
 * A basic integration test for the embedded jetty application.
 */
public class RunnerAppIT {
    private final static Logger LOG = LoggerFactory.getLogger( SecureAppIT.class );


    @Rule
    public JettyJarResource app = new JettyJarResource();

    @Test
    public void testEmbeddedApp() throws Exception {
        LOG.info( "integration testing embedded jetty application executable jar file" );

        CertUtils.preparations( app.getHostname(), app.getPort() );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        String result = client
                .resource( app.getAppProperties().getProperty( Launcher.SERVER_URL ) )
                .path( "/" )
                .accept( MediaType.TEXT_PLAIN )
                .get( String.class );

        assertEquals( HelloWorldServlet.MESSAGE, result );
    }
}
