package org.safehaus.chop.webapp;


import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.RunnerBuilder;
import org.safehaus.chop.webapp.coordinator.rest.ResetResource;
import org.safehaus.chop.webapp.coordinator.rest.RunManagerResource;
import org.safehaus.chop.webapp.coordinator.rest.RunnerRegistryResource;
import org.safehaus.chop.webapp.coordinator.rest.StartResource;
import org.safehaus.chop.webapp.coordinator.rest.StopResource;
import org.safehaus.jettyjam.utils.TestParams;

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
}
