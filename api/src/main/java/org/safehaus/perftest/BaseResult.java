package org.safehaus.perftest;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * ...
 */
public class BaseResult implements Result {
    private String endpoint;
    private String message;
    private boolean status;
    private State state;


    public BaseResult( String endpoint, boolean status, String message, State state ) {
        this.endpoint = endpoint;
        this.status = status;
        this.message = message;
        this.state = state;    }


    @SuppressWarnings( "UnusedDeclaration" )
    public BaseResult() {
        status = true;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public void setEndpoint( String endpoint ) {
        this.endpoint = endpoint;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public void setStatus( boolean status ) {
        this.status = status;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public void setMessage( String message ) {
        this.message = message;
    }


    @JsonProperty
    @Override
    public boolean getStatus() {
        return status;
    }


    @Override
    public State getState() {
        return state;
    }


    @JsonProperty
    @Override
    public String getMessage() {
        return message;
    }


    @JsonProperty
    @Override
    public String getEndpoint() {
        return endpoint;
    }
}
