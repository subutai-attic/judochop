package org.safehaus.jchop.tracker;


import org.safehaus.perftest.api.annotations.IterationChop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Gather all data in one place for an IterationChop.
 */
@JsonPropertyOrder( { "testClass", "iterationChop", "tracker" } )
public class IterationChopTracker extends ChopTracker {
    private final IterationChop iterationChop;


    public IterationChopTracker( final Class<?> testClass ) {
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


    @JsonProperty
    public IterationChop getIterationChop() {
        return iterationChop;
    }
}
