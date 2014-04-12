/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
