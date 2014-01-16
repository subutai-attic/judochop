package org.safehaus.chop.client.rest;


import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static org.safehaus.chop.api.Constants.PARAM_PROJECT;


/**
 * Client REST request functions.
 */
public class RestRequests {
    private static final Logger LOG = LoggerFactory.getLogger( RestRequests.class );

    /**
     * Performs a POST HTTP operation against the /load endpoint with the perftest query parameter, and propagate query
     * parameter.
     *
     * @param runnerFig the runnerFig to perform the load operation on
     * @param project the project query parameter value
     * @param props optional set of store, and tomcat manager configuration parameters
     *
     * @return the result of the operation
     */
    public static Result load( RunnerFig runnerFig, String project, Map<String,String> props ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/load" );
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
     * @param runnerFig the runnerFig which will perform the start operation
     * @return the result of the operation
     */
    public static Result start( RunnerFig runnerFig ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/start" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /reset endpoint with a propagate query parameter.
     *
     * @param runnerFig the runnerFig to perform the reset operation on
     * @return the result of the operation
     */
    public static Result reset( RunnerFig runnerFig ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/reset" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /stop endpoint with a propagate query parameter.
     *
     * @param runnerFig the runnerFig which will perform the stop operation
     * @return the result of the operation
     */
    public static Result stop( RunnerFig runnerFig ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/stop" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
    }


    /**
     * Performs a GET HTTP operation against the /status endpoint.
     *
     * @param runnerFig the runnerFig to perform the status operation on
     *
     * @return the result of the operation
     */
    public static Result status( RunnerFig runnerFig ) {
        Preconditions.checkNotNull( runnerFig, "RunnerFig parameter cannot be null." );

        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/status" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
    }
}
