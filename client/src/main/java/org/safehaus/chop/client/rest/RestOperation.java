package org.safehaus.chop.client.rest;


import com.sun.jersey.api.client.WebResource;


/**
 * A simple request interface.
 */
public interface RestOperation<R> {
    R getResult();

    WebResource getResource();

    String getPath();

    R execute( Class<? extends R> rClass );
}
