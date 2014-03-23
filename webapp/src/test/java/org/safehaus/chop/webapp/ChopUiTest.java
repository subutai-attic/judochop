package org.safehaus.chop.webapp;


import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.webapp.coordinator.rest.TestGetResource;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchResource;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.JettyResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests the ChopUi.
 */
public class ChopUiTest {
    private static final Logger LOG = LoggerFactory.getLogger( ChopUiTest.class );

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
    public static JettyResource jetty = new JettyResource();

    @ClassRule
    public static ElasticSearchResource es = new ElasticSearchResource();


    static {
        CertUtils.preparations( jetty.getHostname(), jetty.getPort() );
    }


    @Test
    public void testGet() {
        String serverUrl = jetty.getServerUrl().toExternalForm();
        WebResource resource = Client.create().resource( serverUrl + TestGetResource.ENDPOINT_URL );
        String result = resource.type( MediaType.TEXT_PLAIN_TYPE ).get( String.class );
        assertEquals( TestGetResource.TEST_MESSAGE, result );
    }


    @Test
    public void testUpload() {
        String serverUrl = jetty.getServerUrl().toExternalForm();
        InputStream in = getClass().getClassLoader().getResourceAsStream( "log4j.properties" );

        FormDataMultiPart part = new FormDataMultiPart();
        part.field( RestParams.FILENAME, "log4j.properties" );

        FormDataBodyPart body = new FormDataBodyPart( UploadResource.CONTENT,
                in, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        part.bodyPart( body );

        LOG.debug( "Server URL = {}", serverUrl );

        WebResource resource = Client.create().resource( serverUrl + UploadResource.ENDPOINT_URL );
        String result = resource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, part );

        LOG.debug( "Got back result = {}", result );
    }
}
