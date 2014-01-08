package org.safehaus.chop.client.rest;


import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.PropagatedResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static org.safehaus.chop.api.Constants.PARAM_PROJECT;
import static org.safehaus.chop.api.Constants.PARAM_PROPAGATE;


/**
 * Client REST request functions.
 */
public class RestRequests {
    /**
     * Performs a POST HTTP operation against the /load endpoint with the perftest query parameter, and propagate query
     * parameter.
     *
     * @param runnerFig the runnerFig to perform the load operation on
     * @param project the project query parameter value
     * @param propagate whether or not to enable propagation
     * @param storeProps optional set of store configuration parameters
     *
     * @return the result of the operation
     */
    public static Result load( RunnerFig runnerFig, String project, Boolean propagate, Map<String,String> storeProps ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/load" );
        resource = resource.queryParam( PARAM_PROPAGATE, propagate.toString() ).queryParam( PARAM_PROJECT, project );

        if ( storeProps != null ) {
            for ( String key : storeProps.keySet() ) {
                resource = resource.queryParam( key, storeProps.get( key ) );
            }
        }

        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /start endpoint with a propagate query parameter.
     *
     * @param runnerFig the runnerFig which will perform the start operation
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result start( RunnerFig runnerFig, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/start" );
        return resource.queryParam( PARAM_PROPAGATE, propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /reset endpoint with a propagate query parameter.
     *
     * @param runnerFig the runnerFig to perform the reset operation on
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result reset( RunnerFig runnerFig, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/reset" );
        return resource.queryParam( PARAM_PROPAGATE, propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /stop endpoint with a propagate query parameter.
     *
     * @param runnerFig the runnerFig which will perform the stop operation
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result stop( RunnerFig runnerFig, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/stop" );
        return resource.queryParam( PARAM_PROPAGATE, propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a GET HTTP operation against the /status endpoint.
     *
     * @param runnerFig the runnerFig to perform the status operation on
     *
     * @return the result of the operation
     */
    public static Result status( RunnerFig runnerFig ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runnerFig.getUrl() ).path( "/status" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
    }
}
