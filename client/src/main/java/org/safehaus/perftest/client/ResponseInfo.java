package org.safehaus.perftest.client;


import java.util.Collection;


public class ResponseInfo {

    private Collection<String> errorMessages;

    private Collection<String> messages;

    private String endpoint;

    private boolean requestSuccessful;

    private boolean operationSuccessful;


    /**
     * @param endpoint
     * @param requestSuccessful
     * @param operationSuccessful
     * @param messages
     * @param errorMessages
     */
    public ResponseInfo( String endpoint, boolean requestSuccessful, boolean operationSuccessful,
                         Collection<String> messages, Collection<String> errorMessages ) {
        this.endpoint = endpoint;
        this.requestSuccessful = requestSuccessful;
        this.operationSuccessful = operationSuccessful;
        this.messages = messages;
        this.errorMessages = errorMessages;
    }


    public Collection<String> getErrorMessages() {
        return errorMessages;
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


    public boolean isOperationSuccessful() {
        return operationSuccessful;
    }


}
