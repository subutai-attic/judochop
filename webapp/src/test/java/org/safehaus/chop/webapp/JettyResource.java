package org.safehaus.chop.webapp;


import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** A Jetty external resource to use for running the tests. */
public class JettyResource extends ExternalResource {
    private final static Logger LOG = LoggerFactory.getLogger( JettyResource.class );
    private final Resource resource;

    private int port;
    private URL serverUrl;
    private Server server;
    private boolean started;


    /**
     * A resource on the test.
     *
     * @param baseResource The name of the resource.
     */
    public JettyResource( String baseResource ) {
        try
        {
            resource = Resource.newResource( baseResource );
        }
        catch ( Exception e ) {
            LOG.error( "Failed to allocate web resource for Jetty: " + baseResource, e );
            throw new RuntimeException( e );
        }
    }


    @Override
    protected void before() throws Throwable {
        server = new Server( 0 );

        org.eclipse.jetty.webapp

        ResourceHandler handler = new ResourceHandler();
        handler.setBaseResource( resource );
        server.setHandler( handler );


        server.start();
        this.port = ( ( ServerConnector ) server.getConnectors()[0] ).getLocalPort();
        this.serverUrl = new URL( "http", "localhost", port, "/" );
        this.started = true;
    }


    @Override
    protected void after() {
        try
        {
            if ( server != null )
            {
                server.stop();
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Could not stop the server", e );
        }
    }


    public int getPort()
    {
        return port;
    }


    public URL getServerUrl()
    {
        return serverUrl;
    }


    public boolean isStarted()
    {
        return started;
    }
}
