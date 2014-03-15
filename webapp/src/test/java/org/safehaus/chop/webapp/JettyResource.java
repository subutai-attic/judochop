package org.safehaus.chop.webapp;


import java.net.URL;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.BaseHolder;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;


/** A Jetty external resource to use for running the tests. */
public class JettyResource extends ExternalResource {
    private final static Logger LOG = LoggerFactory.getLogger( JettyResource.class );

    private int port;
    private URL serverUrl;
    private Server server;
    private boolean started;


    @Override
    protected void before() throws Throwable {
//        WebAppContext ctx = new WebAppContext();
//        ctx.setContextPath( "/" );
//        ctx.setResourceBase( "./webapp/src/main/webapp" );
//        ctx.setDescriptor( "./webapp/src/main/webapp/web.xml" );
//        ctx.setParentLoaderPriority( true );
//        server.setHandler( ctx );

        ServletHandler servletHandler = new ServletHandler();

        // Add the GuiceFilter and mapping for REST calls
        FilterHolder filterHolder = new FilterHolder();
        filterHolder.setFilter( new GuiceFilter() );
        EnumSet<DispatcherType> enumSet = EnumSet.allOf( DispatcherType.class );
        servletHandler.addFilterWithMapping( filterHolder, "/*", enumSet );

        // Add the listener configuration with the chop webapp ServletConfig
        ListenerHolder listenerHolder = new ListenerHolder( BaseHolder.Source.EMBEDDED );
        listenerHolder.setHeldClass( ServletConfig.class );
        listenerHolder.setListener( new ServletConfig() );

        HandlerCollection handlers = new HandlerCollection();
//        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        contexts.addContext( "/", "./webapp/src/main/webapp" );
        handlers.setHandlers( new Handler[] { servletHandler, new DefaultHandler() } );

        server = new Server( 0 );
        server.setHandler( handlers );
        server.start();

        ServerConnector connector = ( ServerConnector ) server.getConnectors()[0];



        this.port = connector.getLocalPort();
        this.serverUrl = new URL( "http", "localhost", port, "" );
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
