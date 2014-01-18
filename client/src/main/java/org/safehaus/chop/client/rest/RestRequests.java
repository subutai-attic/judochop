package org.safehaus.chop.client.rest;


import java.util.Map;

import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static org.safehaus.chop.api.Constants.PARAM_PROJECT;


/**
 * Client REST request functions.
 */
public class RestRequests {
    private static final Logger LOG = LoggerFactory.getLogger( RestRequests.class );


    private static void preparations( Runner runner ) {
        Preconditions.checkNotNull( runner, "The runner parameter cannot be null." );
        Preconditions.checkNotNull( runner.getHostname(), "The runner parameter's hostname property cannot be null." );

        if ( ! ChopUtils.isTrusted( runner ) ) {
            try {
                ChopUtils.installRunnerKey( null, runner );
            }
            catch ( Exception e ) {
                LOG.error( "Failed to install certificate for runner: {}", runner.getHostname() );

                try {
                    ChopUtils.installCert( runner.getHostname(), runner.getServerPort(), null );
                }
                catch ( Exception e2 ) {
                    LOG.error( "Failed to get certificate from server {} on port {}: dumping stack trace!",
                            runner.getHostname(), runner.getServerPort() );
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Performs a POST HTTP operation against the /load endpoint with the perftest query parameter, and propagate query
     * parameter.
     *
     * @param runner the runner to perform the load operation on
     * @param project the project query parameter value
     * @param props optional set of store, and tomcat manager configuration parameters
     *
     * @return the result of the operation
     */
    public static Result load( Runner runner, String project, Map<String,String> props ) {
        preparations( runner );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/load" );
        resource = resource.queryParam( PARAM_PROJECT, project );

        if ( props != null ) {
            for ( String key : props.keySet() ) {
                assert key != null;
                assert props.get( key ) != null;
                LOG.info( "Added load request parameter {} = {}", key, props.get( key ) );
                resource = resource.queryParam( key, props.get( key ) );
            }
        }

        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /start endpoint with a propagate query parameter.
     *
     * @param runner the runner which will perform the start operation
     * @return the result of the operation
     */
    public static Result start( Runner runner ) {
        preparations( runner );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/start" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /reset endpoint with a propagate query parameter.
     *
     * @param runner the runner to perform the reset operation on
     * @return the result of the operation
     */
    public static Result reset( Runner runner ) {
        preparations( runner );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/reset" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /stop endpoint with a propagate query parameter.
     *
     * @param runner the runner which will perform the stop operation
     * @return the result of the operation
     */
    public static Result stop( Runner runner ) {
        preparations( runner );
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/stop" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a GET HTTP operation against the /status endpoint.
     *
     * @param runner the runner to perform the status operation on
     *
     * @return the result of the operation
     */
    public static Result status( Runner runner ) {
        preparations( runner );

        try {
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            Client client = Client.create( clientConfig );
            WebResource resource = client.resource( runner.getUrl() ).path( "/status" );
            return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
        }
        catch ( ClientHandlerException e ) {
            if ( e.getCause() instanceof SSLHandshakeException &&
                    e.getCause().toString().contains( "PKIX path building failed" ) ) {

                /*
                 * Oddly this fails the first time but works the second time. Until
                 * I get to the bottom of this and figure it out this is the work
                 * around we will use to make sure this does not fail. We retry once
                 * on the failure.
                 */

                DefaultClientConfig clientConfig = new DefaultClientConfig();
                Client client = Client.create( clientConfig );
                WebResource resource = client.resource( runner.getUrl() ).path( "/status" );
                return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
            }
        }

        throw new RuntimeException( "If we got here then the retry also failed." );
    }
}
