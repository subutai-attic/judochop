package org.safehaus.chop.client.rest;


import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.Runner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;


/**
 * Abstract rest operation with boilerplate.
 */
public abstract class AbstractRestOperation<R> implements RestOperation<R> {
    private WebResource resource;
    private R result;
    private final String path;
    private final HttpOp op;


    public AbstractRestOperation( HttpOp op, WebResource resource ) {
        this.resource = resource;
        this.path = null;
        this.op = op;
    }


    public AbstractRestOperation( HttpOp op, String path, Runner runner ) {
        this.resource = Client.create().resource( runner.getUrl() ).path( getPath() );
        this.path = path;
        this.op = op;
    }


    public HttpOp getOp() {
        return op;
    }


    protected R setResult( R result ) {
        this.result = result;
        return result;
    }

    @Override
    public R getResult() {
        return result;
    }


    @Override
    public WebResource getResource() {
        return resource;
    }


    @Override
    public String getPath() {
        return path;
    }


    @Override
    public R execute() {
        switch ( op ) {
            case GET:
                return setResult( getResource().accept(
                        MediaType.APPLICATION_JSON_TYPE ).get( new GenericType<R>() {} ) );
            case POST:
                return setResult( getResource().accept(
                        MediaType.APPLICATION_JSON_TYPE ).post( new GenericType<R>() {} ) );
            case PUT:
                return setResult( getResource().accept(
                        MediaType.APPLICATION_JSON_TYPE ).put( new GenericType<R>() {} ) );
            case DELETE:
                return setResult( getResource().accept(
                        MediaType.APPLICATION_JSON_TYPE ).delete( new GenericType<R>() {} ) );
            default:
                throw new IllegalStateException( "Unknown HTTP operation type " + op );
        }
    }
}
