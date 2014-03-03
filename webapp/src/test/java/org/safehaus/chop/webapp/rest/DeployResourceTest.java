package org.safehaus.chop.webapp.rest;


import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.safehaus.chop.webapp.JettyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.multipart.FormDataBodyPart;
//import com.sun.jersey.multipart.FormDataMultiPart;


/** Tests the DeployResource REST web service. */
public class DeployResourceTest {
    private final static Logger LOG = LoggerFactory.getLogger( DeployResourceTest.class );

    @Rule
    public JettyResource jetty = new JettyResource( "./webapp/src/main/webapp/" );// DeployResource.ENDPOINT_URL );


    @Test
    public void testUpload() {/*
        InputStream in = getClass().getClassLoader().getResourceAsStream( "test.war" );

        FormDataMultiPart part = new FormDataMultiPart();
        part.field( DeployResource.FILENAME_PARAM, "test.war" );

        FormDataBodyPart body = new FormDataBodyPart( DeployResource.CONTENT,
                in, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        part.bodyPart( body );

        LOG.debug( "Server URL = {}", jetty.getServerUrl().toString() );

        WebResource resource = Client.create().resource( jetty.getServerUrl().toString() + DeployResource.ENDPOINT_URL );
        String result = resource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, part );

        LOG.debug( "Got back result = {}", result );
    */}
}
