package org.safehaus.chop.client.rest;


import java.util.concurrent.Callable;

import org.safehaus.chop.api.Result;


/**
 * An asynchronous request.
 */
public class AsyncRequest<T,R extends RestOperation> implements Callable<Result> {
    private final R operation;
    private T associate;
    private Exception exception;


    public AsyncRequest( T associate, R operation ) {
        this.operation = operation;
        this.associate = associate;
    }


    public AsyncRequest( R operation ) {
        this.operation = operation;
    }


    public T getAssociate() {
        return associate;
    }


    public R getRestOperation() {
        return operation;
    }


    public Exception getException() {
        return exception;
    }


    public boolean failed() {
        return exception != null;
    }


    @Override
    public Result call() throws Exception {
        try {
            return operation.execute();
        }
        catch ( Exception e ) {
            this.exception = e;
            throw e;
        }
    }
}
