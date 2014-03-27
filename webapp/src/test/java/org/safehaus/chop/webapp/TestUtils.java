package org.safehaus.chop.webapp;


import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.jettyjam.utils.CertUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;


/**
 * Putting some test methods here to be reused by both by unit and integration tests.
 */
public class TestUtils {

    public static String testGet( TestData testData ) {
        try {
            CertUtils.installCert( testData.getHostname(), testData.getPort(), null );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        WebResource resource = Client.create().resource( testData.getServerUrl() ).path( testData.getEndpoint() );
        return resource.type( MediaType.TEXT_PLAIN_TYPE ).get( String.class );
    }


    public static String testUpload( TestData testData ) {
        try {
            CertUtils.installCert( testData.getHostname(), testData.getPort(), null );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        InputStream in = TestUtils.class.getClassLoader().getResourceAsStream( "log4j.properties" );

        FormDataMultiPart part = new FormDataMultiPart();
        part.field( RestParams.FILENAME, "log4j.properties" );

        FormDataBodyPart body = new FormDataBodyPart( UploadResource.CONTENT,
                in, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        part.bodyPart( body );

        testData.getLogger().debug( "Server URL = {}", testData.getServerUrl() );

        WebResource resource = Client.create().resource( testData.getServerUrl() ).path( testData.getEndpoint() );
        String result = resource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, part );

        testData.getLogger().debug( "Got back result = {}", result );
        return result;
    }
}
