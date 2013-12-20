package org.safehaus.jchop.tracker;


import org.safehaus.perftest.api.annotations.TimeChop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Gather all data in one place for a TimeChop.
 */
@JsonPropertyOrder( { "testClass", "iterationChop", "tracker" } )
public class TimeChopTracker extends ChopTracker {
    private final TimeChop timeChop;


    public TimeChopTracker( Class<?> testClass ) {
        super( testClass );
        this.timeChop = testClass.getAnnotation( TimeChop.class );
    }


    @Override
    public long getDelay() {
        return timeChop.delay();
    }


    @Override
    public int getThreads() {
        return timeChop.threads();
    }


    @JsonProperty
    public TimeChop getTimeChop() {
        return timeChop;
    }
}
