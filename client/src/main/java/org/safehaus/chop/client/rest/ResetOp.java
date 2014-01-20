package org.safehaus.chop.client.rest;


import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;

import com.amazonaws.services.ec2.model.Instance;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/**
 * A reset request.
 */
public class ResetOp implements RestOperation {
    public static final String PATH = "/reset";

    private String resource;
    private Result result;


    @SuppressWarnings( "UnusedDeclaration" )
    public ResetOp( String resource ) {
        this.resource = resource;
    }


    public ResetOp( Instance instance ) {
        this.resource = "https://" + instance.getPublicDnsName() + ":" + Runner.DEFAULT_SERVER_PORT;
    }


    @Override
    public Result getResult() {
        return result;
    }


    @Override
    public String getResource() {
        return resource;
    }


    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }


    @Override
    public String getPath() {
        return PATH;
    }


    @Override
    public Result execute() {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( this.resource ).path( PATH );
        result = resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
        return result;
    }
}
