package org.apache.usergrid.chop.runner.drivers;


import org.apache.usergrid.chop.api.TimeChop;

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
    public boolean getSaturate() {
        return timeChop.saturate();
    }


    @JsonProperty
    public TimeChop getTimeChop() {
        return timeChop;
    }
}
