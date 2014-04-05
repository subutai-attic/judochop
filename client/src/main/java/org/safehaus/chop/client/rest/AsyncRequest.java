package org.safehaus.chop.client.rest;


import java.util.concurrent.Callable;


/**
 * An asynchronous request.
 */
public class AsyncRequest<A,R, O extends RestOperation<R>> implements Callable<R> {
    private final O operation;
    private A associate;
    private Exception exception;


    public AsyncRequest( A associate, O operation ) {
        this.operation = operation;
        this.associate = associate;
    }


    public AsyncRequest( O operation ) {
        this.operation = operation;
    }


    public A getAssociate() {
        return associate;
    }


    public O getRestOperation() {
        return operation;
    }


    public Exception getException() {
        return exception;
    }


    public boolean failed() {
        return exception != null;
    }


    @Override
    public R call() throws Exception {
        try {
            return operation.execute();
        }
        catch ( Exception e ) {
            this.exception = e;
            throw e;
        }
    }
}
