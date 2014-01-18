package org.safehaus.chop.client.rest;


import java.util.Map;

import org.safehaus.chop.api.Result;


/**
 * A simple request interface.
 */
public interface RestOperation {
    Result getResult();

    String getResource();

    Map<String,String> getParameters();

    String getPath();

    Result execute();
}
