package org.safehaus.perftest.api;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


/**
 * ...
 */
public class PropagatedResult extends BaseResult {
    private List<Result> remoteResults = new ArrayList<Result>();


    public PropagatedResult( String endpoint, boolean status, String message, State state ) {
        super( endpoint, status, message, state );
    }


    public void add( Result result ) {
        remoteResults.add( result );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    @JsonProperty
    public List<Result> getRemoteResults()
    {
        return remoteResults;
    }
}
