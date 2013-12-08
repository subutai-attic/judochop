package org.safehaus.perftest.client.rest;


import java.net.UnknownHostException;

import javax.ws.rs.core.MediaType;

import org.safehaus.perftest.api.PropagatedResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/9/13 Time: 1:01 AM To change this template use File | Settings |
 * File Templates.
 */
public class LoadRequest {
    public Result load( RunnerInfo runner, String testKey ) {
        Result result;
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/load" );
        result = resource
                .queryParam( "propagate", "true" )
                .queryParam( "perftest", testKey )
                .accept( MediaType.APPLICATION_JSON_TYPE ).post( PropagatedResult.class );

        return result;
    }
}
