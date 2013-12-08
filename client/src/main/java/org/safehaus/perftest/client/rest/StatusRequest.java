package org.safehaus.perftest.client.rest;


import java.net.UnknownHostException;

import javax.ws.rs.core.MediaType;

import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/8/13 Time: 10:30 PM To change this template use File | Settings
 * | File Templates.
 */
public class StatusRequest {
    public Result status( RunnerInfo runner ) throws UnknownHostException {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( runner.getUrl() ).path( "/status" );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
    }
}
