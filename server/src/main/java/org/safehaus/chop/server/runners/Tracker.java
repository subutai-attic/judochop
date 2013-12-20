package org.safehaus.chop.server.runners;


import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;


/**
 * Tracks a Chop!
 */
public abstract class Tracker {
    public static final int INVALID_TIME = -1;
    protected final Class<?> testClass;
    private final LinkedList<Result> results = new LinkedList<Result>();
    private long startTime = INVALID_TIME;
    private long stopTime = INVALID_TIME;
    private AtomicInteger testClassRuns = new AtomicInteger( 0 );
    private long maxTime;
    private long minTime;
    private long meanTime;



    public Tracker( final Class<?> testClass ) {
        this.testClass = testClass;
    }


    @JsonProperty
    public Class<?> getTestClass() {
        return testClass;
    }


    @JsonProperty
    public LinkedList<Result> getResults() {
        return results;
    }


    public Result execute() {
        Result result = new JUnitCore().run( testClass );
        results.addFirst( result );
        testClassRuns.incrementAndGet();
        return result;
    }


    @JsonProperty
    public abstract long getDelay();

    @JsonProperty
    public abstract int getThreads();

    @JsonProperty
    public abstract int getRunners();


    @JsonProperty
    public long getDuration() {
        Preconditions.checkState( startTime != INVALID_TIME,
                "The startTime has not been set: check if the test was even started." );
        Preconditions.checkState( stopTime != INVALID_TIME,
                "The stopTime has not been set: check that the test completed." );

        return stopTime - startTime;
    }


    // @TODO - need to dequeue and push to a file in a separate thread
    private Result dequeue( long timeout ) throws InterruptedException {
        synchronized ( results ) {
            if ( results.isEmpty() ) {
                results.wait( timeout );
            }

            if ( results.isEmpty() ) {
                return null;
            }

            return results.removeLast();
        }
    }


    @JsonProperty
    public long getStartTime() {
        return startTime;
    }


    public void setStartTime( final long startTime ) {
        this.startTime = startTime;
    }


    @JsonProperty
    public long getStopTime() {
        return stopTime;
    }


    public void setStopTime( final long stopTime ) {
        this.stopTime = stopTime;
    }


    public void reset() {
        results.clear();
        setStartTime( INVALID_TIME );
        setStopTime( INVALID_TIME );
        testClassRuns.set( 0 );
        maxTime = 0;
        minTime = 0;
        meanTime = 0;
    }


    @JsonProperty
    public long getMaxTime() {
        return maxTime;
    }


    @JsonProperty
    public long getMinTime() {
        return minTime;
    }


    @JsonProperty
    public long getMeanTime() {
        return meanTime;
    }
}
