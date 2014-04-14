/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.webapp;


import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.usergrid.chop.api.BaseResult;
import org.apache.usergrid.chop.api.RestParams;
import org.apache.usergrid.chop.api.Runner;
import org.apache.usergrid.chop.api.RunnerBuilder;
import org.apache.usergrid.chop.webapp.coordinator.rest.ResetResource;
import org.apache.usergrid.chop.webapp.coordinator.rest.RunnerRegistryResource;
import org.apache.usergrid.chop.webapp.coordinator.rest.StopResource;
import org.apache.usergrid.chop.webapp.coordinator.rest.RunManagerResource;
import org.apache.usergrid.chop.webapp.coordinator.rest.StartResource;
import org.apache.usergrid.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.jettyjam.utils.TestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import com.sun.jersey.api.client.GenericType;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Common tests run in unit and test mode.
 */
public class ChopUiTestUtils {

    public static Map<String, String> getQueryParams() {

        Map<String, String> params = new HashMap<String, String>();

        params.put( RestParams.USERNAME, "user" );
        params.put( RestParams.PASSWORD, "pass" );
        params.put( RestParams.COMMIT_ID, UUID.randomUUID().toString() );
        params.put( RestParams.MODULE_VERSION, "2.0.0-SNAPSHOT" );
        params.put( RestParams.MODULE_ARTIFACTID, "chop-example" );
        params.put( RestParams.MODULE_GROUPID, "org.apache.usergrid.chop" );
        params.put( RestParams.TEST_PACKAGE, "org.apache.usergrid.chop.example" );

        return params;
    }

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


    public static void testRunnerRegistryUnregister( TestParams testParams ) {
        Boolean result = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource()
                .path( "/unregister" )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( Boolean.class );

        assertFalse( result );
    }


    public static void testRunnerRegistryRegister( TestParams testParams ) {
        /*
         * Even though in test mode the runner is not used, a runner must
         * be sent over because the method is expecting the Runner as JSON.
         */
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


    public static void testRunnerRegistrySequence( TestParams testParams ) {
        /*
         * ------------------------------------------------------------
         * Let's register a runner first before we query for it
         * ------------------------------------------------------------
         */

        String commitId = UUID.randomUUID().toString();
        String hostname = RandomStringUtils.randomAlphabetic( 8 );

        RunnerBuilder builder = new RunnerBuilder();
        builder.setTempDir( "." )
                .setServerPort( 19023 )
                .setUrl( "https://localhost:19023" )
                .setHostname( hostname )
                .setIpv4Address( "127.0.0.1" );

        Boolean result = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource( null )
                .queryParam( RestParams.COMMIT_ID, commitId )
                .path( "/register" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( Boolean.class, builder.getRunner() );

        assertTrue( result );

        /*
         * ------------------------------------------------------------
         * Let's see if we can get the runner back from the registry
         * ------------------------------------------------------------
         */
        List<Runner> runnerList = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource( null )
                .queryParam( RestParams.COMMIT_ID, commitId )
                .path( "/list" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .get( new GenericType<List<Runner>>() {} );

        assertNotNull( runnerList );
        assertEquals( 1, runnerList.size() );

        Runner runner = runnerList.get( 0 );
        assertEquals( 19023, runner.getServerPort() );
        assertEquals( "https://localhost:19023", runner.getUrl() );
        assertEquals( hostname, runner.getHostname() );
        assertEquals( "127.0.0.1", runner.getIpv4Address() );
        assertEquals( ".", runner.getTempDir() );

        /*
         * ------------------------------------------------------------
         * Let's unregister the runner from the registry and check
         * ------------------------------------------------------------
         */
        result = testParams
                .newWebResource( null )
                .queryParam( RestParams.RUNNER_URL, runner.getUrl() )
                .path( "/unregister" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .post( Boolean.class );

        assertTrue( result );

        /*
         * ------------------------------------------------------------
         * Let's make sure we do NOT get the runner from the registry
         * ------------------------------------------------------------
         */
        runnerList.clear();
        runnerList = testParams
                .setEndpoint( RunnerRegistryResource.ENDPOINT )
                .newWebResource( null )
                .queryParam( RestParams.COMMIT_ID, commitId )
                .path( "/list" )
                .type( MediaType.APPLICATION_JSON_TYPE )
                .accept( MediaType.APPLICATION_JSON_TYPE )
                .get( new GenericType<List<Runner>>() {} );

        assertNotNull( runnerList );
        assertEquals( 0, runnerList.size() );
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
        bodyPart.setText( "org.apache.usergrid.chop" );
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
        bodyPart.setText( "org.apache.usergrid.safehaus.chop.example" );
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
