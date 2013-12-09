package org.safehaus.perftest.client.rest;


import javax.ws.rs.core.MediaType;

import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.api.PropagatedResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/9/13 Time: 3:15 AM To change this template use File | Settings |
 * File Templates.
 */
public class RestRequests {
    /**
     * Performs a POST HTTP operation against the /load endpoint with the perftest query parameter, and propagate query
     * parameter.
     *
     * @param runner the runner to perform the load operation on
     * @param perftest the perftest query parameter value
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result load( RunnerInfo runner, String perftest, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/load" );
        return resource.queryParam( "propagate", propagate.toString() ).queryParam( "perftest", perftest )
                       .accept( MediaType.APPLICATION_JSON_TYPE ).post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /start endpoint with a propagate query parameter.
     *
     * @param runner the runner which will perform the start operation
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result start( RunnerInfo runner, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/start" );
        return resource.queryParam( "propagate", propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /reset endpoint with a propagate query parameter.
     *
     * @param runner the runner to perform the reset operation on
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result reset( RunnerInfo runner, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/reset" );
        return resource.queryParam( "propagate", propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a POST HTTP operation against the /stop endpoint with a propagate query parameter.
     *
     * @param runner the runner which will perform the stop operation
     * @param propagate whether or not to enable propagation
     *
     * @return the result of the operation
     */
    public static Result stop( RunnerInfo runner, Boolean propagate ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/stop" );
        return resource.queryParam( "propagate", propagate.toString() ).accept( MediaType.APPLICATION_JSON_TYPE )
                       .post( PropagatedResult.class );
    }


    /**
     * Performs a GET HTTP operation against the /status endpoint.
     *
     * @param runner the runner to perform the status operation on
     *
     * @return the result of the operation
     */
    public static Result status( RunnerInfo runner ) {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/status" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
    }
}
