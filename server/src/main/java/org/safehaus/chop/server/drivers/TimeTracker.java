package org.safehaus.chop.server.drivers;


import org.safehaus.chop.api.annotations.TimeChop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Gather all data in one place for a TimeChop.
 */
@JsonPropertyOrder( { "testClass", "iterationChop", "tracker" } )
public class TimeTracker extends Tracker {
    private final TimeChop timeChop;


    public TimeTracker( Class<?> testClass ) {
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


    @Override
    public int getRunners() {
        return timeChop.runners();
    }


    @JsonProperty
    public TimeChop getTimeChop() {
        return timeChop;
    }
}
