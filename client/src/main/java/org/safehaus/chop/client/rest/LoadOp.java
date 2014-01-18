package org.safehaus.chop.client.rest;


import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import static org.safehaus.chop.api.Constants.PARAM_PROJECT;


/**
 * A load request.
 */
public class LoadOp implements RestOperation {
    public static final String PATH = "/load";
    private static final Logger LOG = LoggerFactory.getLogger( LoadOp.class );

    private Map<String,String> parameters;
    private String resource;
    private Result result;


    public LoadOp( String resource ) {
        this.resource = resource;
    }


    public LoadOp( Instance instance, Map<String,String> parameters ) {
        Preconditions.checkNotNull( instance );
        Preconditions.checkNotNull( parameters );
        Preconditions.checkArgument( parameters.containsKey( PARAM_PROJECT ) );
        Preconditions.checkNotNull( parameters.containsKey( PARAM_PROJECT ) );

        this.resource = "https://" + instance.getPublicDnsName() + ":" + Runner.DEFAULT_SERVER_PORT;
        this.parameters = parameters;
    }


    public LoadOp( Runner runner, Map<String,String> parameters ) {
        Preconditions.checkNotNull( runner );
        Preconditions.checkNotNull( parameters );
        Preconditions.checkArgument( parameters.containsKey( PARAM_PROJECT ) );
        Preconditions.checkNotNull( parameters.containsKey( PARAM_PROJECT ) );

        this.resource = "https://" + runner.getHostname() + ":" + runner.getServerPort();
        this.parameters = parameters;
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
        return parameters;
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

        resource = resource.queryParam( PARAM_PROJECT, parameters.get( PARAM_PROJECT ) );

        for ( String key : parameters.keySet() ) {
            assert key != null;
            assert parameters.get( key ) != null;
            LOG.info( "Added load request parameter {} = {}", key, parameters.get( key ) );
            resource = resource.queryParam( key, parameters.get( key ) );
        }

        result = resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
        return result;
    }
}
