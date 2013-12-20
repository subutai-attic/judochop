package org.safehaus.perftest.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/** Information about a run. */
public class RunInfo {
    private final long runtime;
    private long callCount;
    private int threadCount;
    private int runnerCount;
    private long delayBetweenCalls;
    private long duration;
    private final int runNumber;
    private CallStatsSnapshot callStatsSnapshot;


    public RunInfo( int runNumber ) {
        this.runNumber = runNumber;
        this.runtime = System.currentTimeMillis();
    }


    @JsonProperty
    public long getCallCount() {
        return callCount;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setCallCount( long callCount ) {
        this.callCount = callCount;
    }


    @JsonProperty
    public int getThreadCount() {
        return threadCount;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setThreadCount( int threadCount ) {
        this.threadCount = threadCount;
    }


    @JsonProperty
    public int getRunnerCount() {
        return runnerCount;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setRunnerCount( int runnerCount ) {
        this.runnerCount = runnerCount;
    }


    @JsonProperty
    public long getDelayBetweenCalls() {
        return delayBetweenCalls;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setDelayBetweenCalls( long delayBetweenCalls ) {
        this.delayBetweenCalls = delayBetweenCalls;
    }


    @JsonProperty
    public long getRuntime() {
        return runtime;
    }


    @JsonProperty
    public long getDuration() {
        return duration;
    }


    @SuppressWarnings("UnusedDeclaration")
    public void setDuration( long duration ) {
        this.duration = duration;
    }


    @JsonProperty
    public CallStatsSnapshot getCallStatsSnapshot() {
        return callStatsSnapshot;
    }


    @JsonProperty
    public int getRunNumber() {
        return runNumber;
    }
}
