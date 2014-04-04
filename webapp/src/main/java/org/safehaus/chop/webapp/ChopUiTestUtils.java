package org.safehaus.chop.webapp;


import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.RunnerBuilder;
import org.safehaus.chop.webapp.coordinator.rest.ResetResource;
import org.safehaus.chop.webapp.coordinator.rest.RunManagerResource;
import org.safehaus.chop.webapp.coordinator.rest.RunnerRegistryResource;
import org.safehaus.chop.webapp.coordinator.rest.StartResource;
import org.safehaus.chop.webapp.coordinator.rest.StopResource;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.jettyjam.utils.TestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;


/**
 * Common tests run in unit and test mode.
 */
public class ChopUiTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger( ChopUiTestUtils.class );


    public static void testRunManagerNext( TestParams testParams ) {
        Integer next = testParams
                .setEndpoint( RunManagerResource.ENDPOINT )
                .newWebResource()
                .path( "/next" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON )
                .get( Integer.class );

        assertEquals( 0, next.intValue() );
    }


    public static void testRunnerRegistryList( TestParams testParams ) {
        List<Runner> runnerList = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource()
                .path( "/list" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .get( new GenericType<List<Runner>>() {} );

        assertNotNull( runnerList );
        assertEquals( 0, runnerList.size() );
    }


    public static void testRunnerRegistryRegister( TestParams testParams ) {
        RunnerBuilder builder = new RunnerBuilder();
        builder.setTempDir( "." )
                .setServerPort( 19023 )
                .setUrl( "https://localhost:19023" )
                .setHostname( "foobar" )
                .setIpv4Address( "127.0.0.1" );

        Boolean result = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource()
                .path( "/register" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( Boolean.class, builder.getRunner() );

        assertFalse( result );
    }


    public static void testStart( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( StartResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), StartResource.ENDPOINT );
    }


    public static void testReset( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( ResetResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), ResetResource.ENDPOINT );
    }


    public static void testStop( TestParams testParams ) {
        BaseResult result = testParams
                .setEndpoint( StopResource.ENDPOINT )
                .newWebResource()
                .accept( MediaType.APPLICATION_JSON )
                .post( BaseResult.class );

        assertEquals( result.getEndpoint(), StopResource.ENDPOINT );
    }


    public static void testUpload( TestParams testParams ) throws Exception {

        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.COMMIT_ID );
        bodyPart.setText( "a0967e74d95c0df8527098ec4755a898ddba6fea" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.MODULE_ARTIFACTID );
        bodyPart.setText( "chop-example" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.MODULE_GROUPID );
        bodyPart.setText( "org.safehaus.chop" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.MODULE_VERSION );
        bodyPart.setText( "2.0.0-SNAPSHOT" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.USERNAME );
        bodyPart.setText( "test-user" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.FILENAME );
        bodyPart.setText( "runner.jar" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.VCS_REPO_URL );
        bodyPart.setText( "ssh://git@stash.safehaus.org:7999/chop/main.git" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.TEST_PACKAGE );
        bodyPart.setText( "org.safehaus.chop.example" );
        multipart.addBodyPart( bodyPart );

        bodyPart = new MimeBodyPart();
        bodyPart.setContentID( RestParams.MD5 );
        bodyPart.setText( "3010c538d1b582ee2d26c9aae7a73186" );
        multipart.addBodyPart( bodyPart );

        File tmpFile = File.createTempFile( "runner", "jar" );
        bodyPart = new MimeBodyPart( new FileInputStream( tmpFile ) );
        bodyPart.setContentID( RestParams.CONTENT );
        multipart.addBodyPart( bodyPart );

        ClientResponse response = testParams
                            .setEndpoint( UploadResource.ENDPOINT )
                            .newWebResource()
                            .path( "/runner" )
                            .type( MediaType.MULTIPART_FORM_DATA )
                            .accept( MediaType.TEXT_PLAIN )
                            .post( ClientResponse.class, multipart );

        assertEquals( Response.Status.CREATED.getStatusCode(), response.getStatus() );

        assertEquals( "Test parameters are OK", response.getEntity( String.class ) );

        tmpFile.delete();
    }
}
