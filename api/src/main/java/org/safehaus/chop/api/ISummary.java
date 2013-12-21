package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * A summary of a single chop run.
 */
public interface ISummary {
    @JsonProperty
    int getRunNumber();

    @JsonProperty
    long getIterations();

    @JsonProperty
    long getTotalTestsRun();

    @JsonProperty
    String getTestName();

    @JsonProperty
    String getChopType();

    @JsonProperty
    int getThreads();

    @JsonProperty
    int getRunners();

    @JsonProperty
    long getDelay();

    @JsonProperty
    long getTime();

    @JsonProperty
    long getActualTime();

    @JsonProperty
    long getMinTime();

    @JsonProperty
    long getMaxTime();

    @JsonProperty
    long getMeanTime();

    @JsonProperty
    long getFailures();

    @JsonProperty
    long getIgnores();

    @JsonProperty
    long getStartTime();

    @JsonProperty
    long getStopTime();

    @JsonProperty
    boolean getSaturate();
}
