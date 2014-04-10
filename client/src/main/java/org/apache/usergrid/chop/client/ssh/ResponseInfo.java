package org.apache.usergrid.chop.client.ssh;


import java.util.Collection;


public class ResponseInfo {

    private Collection<String> errorMessages;

    private Collection<String> messages;

    private String endpoint;

    private boolean requestSuccessful;

    private boolean operationSuccessful;


    public ResponseInfo( String endpoint, boolean requestSuccessful, boolean operationSuccessful,
                         Collection<String> messages, Collection<String> errorMessages ) {
        this.endpoint = endpoint;
        this.requestSuccessful = requestSuccessful;
        this.operationSuccessful = operationSuccessful;
        this.messages = messages;
        this.errorMessages = errorMessages;
    }


    void addErrorMessage( String error ) {
        errorMessages.add( error );
    }


    public Collection<String> getErrorMessages() {
        return errorMessages;
    }


    void addMessage( String mesg ) {
        messages.add( mesg );
    }


    public Collection<String> getMessages() {
        return messages;
    }


    public String getEndpoint() {
        return endpoint;
    }


    public boolean isRequestSuccessful() {
        return requestSuccessful;
    }


    void setRequestSuccessful( boolean requestSuccessful ) {
        this.requestSuccessful = requestSuccessful;
    }


    public boolean isOperationSuccessful() {
        return operationSuccessful;
    }


    void setOperationSuccessful( boolean operationSuccessful ) {
        this.operationSuccessful = operationSuccessful;
    }
}
