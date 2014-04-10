package org.apache.usergrid.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/** ... */
public class BaseResult implements Result {
    private String endpoint;
    private String message;
    private boolean status;
    private State state;
    private Project project;


    public BaseResult( String endpoint, boolean status, String message, State state ) {
        this.endpoint = endpoint;
        this.status = status;
        this.message = message;
        this.state = state;
    }


    public BaseResult( String endpoint, boolean status, String message, State state, Project project ) {
        this.endpoint = endpoint;
        this.status = status;
        this.message = message;
        this.state = state;
        this.project = project;
    }


    @SuppressWarnings("UnusedDeclaration")
    public BaseResult() {
        status = true;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setEndpoint( String endpoint ) {
        this.endpoint = endpoint;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setStatus( boolean status ) {
        this.status = status;
    }


    @SuppressWarnings("UnusedDeclaration")
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


    public void setState( State state ) {
        this.state = state;
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


    @Override
    @JsonProperty
    public Project getProject() {
        return project;
    }


    public void setProject( final Project project ) {
        this.project = project;
    }
}
