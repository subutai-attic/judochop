package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/20/13 Time: 3:58 PM To change this template use File | Settings
 * | File Templates.
 */
public interface ISummary {
    @JsonProperty
    long getIterations();

    @SuppressWarnings("UnusedDeclaration")
    void setIterations( long iterations );

    @JsonProperty
    int getThreadCount();

    @SuppressWarnings("UnusedDeclaration")
    void setThreadCount( int threadCount );

    @JsonProperty
    int getRunnerCount();

    @SuppressWarnings("UnusedDeclaration")
    void setRunnerCount( int runnerCount );

    @JsonProperty
    long getDelayBetweenCalls();

    @SuppressWarnings("UnusedDeclaration")
    void setDelayBetweenCalls( long delayBetweenCalls );

    @JsonProperty
    long getRuntime();

    @JsonProperty
    long getDuration();

    @SuppressWarnings("UnusedDeclaration")
    void setDuration( long duration );

    @JsonProperty
    StatsSnapshot getStatsSnapshot();

    @JsonProperty
    int getRunNumber();

    @JsonProperty
    String getTestName();

    void setTestName( String testName );

    @JsonProperty
    String getChopType();

    void setChopType( String chopType );

    @JsonProperty
    long getMinTime();

    void setMinTime( long minTime );

    @JsonProperty
    long getMaxTime();

    void setMaxTime( long maxTime );

    @JsonProperty
    long getMeanTime();

    void setMeanTime( long meanTime );
}
