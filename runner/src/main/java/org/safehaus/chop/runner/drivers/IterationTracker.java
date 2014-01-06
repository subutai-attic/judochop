package org.safehaus.chop.runner.drivers;


import org.safehaus.chop.api.IterationChop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Gather all data in one place for an IterationChop.
 */
@JsonPropertyOrder( { "testClass", "iterationChop", "tracker" } )
public class IterationTracker extends Tracker {
    private final IterationChop iterationChop;


    public IterationTracker( final Class<?> testClass ) {
        super( testClass );
        this.iterationChop = testClass.getAnnotation( IterationChop.class );
    }


    @Override
    public long getDelay() {
        return iterationChop.delay();
    }


    @Override
    public int getThreads() {
        return iterationChop.threads();
    }


    @Override
    public int getRunners() {
        return iterationChop.runners();
    }


    @Override
    public boolean getSaturate() {
        return iterationChop.saturate();
    }


    @JsonProperty
    public IterationChop getIterationChop() {
        return iterationChop;
    }
}
