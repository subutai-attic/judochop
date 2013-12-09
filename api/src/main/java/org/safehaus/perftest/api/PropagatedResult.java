package org.safehaus.perftest.api;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/** ... */
public class PropagatedResult extends BaseResult {
    private List<Result> remoteResults = new ArrayList<Result>();


    public PropagatedResult() {
    }


    public PropagatedResult( String endpoint, boolean status, String message, State state ) {
        super( endpoint, status, message, state );
    }


    public void add( Result result ) {
        remoteResults.add( result );
    }


    @SuppressWarnings("UnusedDeclaration")
    @JsonProperty
    public BaseResult[] getRemoteResults() {
        return remoteResults.toArray( new BaseResult[] { } );
    }
}
