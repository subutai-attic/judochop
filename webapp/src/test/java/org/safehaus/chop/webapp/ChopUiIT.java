package org.safehaus.chop.webapp;


import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.webapp.rest.TestGetResource;
import org.safehaus.chop.webapp.rest.UploadResource;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.JettyJarResource;
import org.safehaus.embedded.jetty.utils.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;


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


    @Test
    public void testGet() {
        CertUtils.preparations( jetty.getHostname(), jetty.getPort() );
        String serverUrl = jetty.getAppProperties().getProperty( Launcher.SERVER_URL );
        WebResource resource = Client.create().resource( serverUrl + TestGetResource.ENDPOINT_URL );
        String result = resource.type( MediaType.TEXT_PLAIN_TYPE ).get( String.class );
    }


    @Test
    public void testUpload() {
        CertUtils.preparations( jetty.getHostname(), jetty.getPort() );
        String serverUrl = jetty.getAppProperties().getProperty( Launcher.SERVER_URL );
        InputStream in = getClass().getClassLoader().getResourceAsStream( "log4j.properties" );

        FormDataMultiPart part = new FormDataMultiPart();
        part.field( UploadResource.FILENAME_PARAM, "log4j.properties" );

        FormDataBodyPart body = new FormDataBodyPart( UploadResource.CONTENT,
                in, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        part.bodyPart( body );

        LOG.debug( "Server URL = {}", serverUrl );

        WebResource resource = Client.create().resource( serverUrl + UploadResource.ENDPOINT_URL );
        String result = resource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, part );

        LOG.debug( "Got back result = {}", result );
    }
}