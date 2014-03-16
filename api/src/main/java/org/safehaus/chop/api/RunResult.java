package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single test run of a specific version of a Maven Module under test.
 */
public interface RunResult {

    String getId();

    String getRunId();

    int getRunCount();

    int getRunTime();

    int getIgnoreCount();

    int getFailureCount();

    String getFailures();
}
