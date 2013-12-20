package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Summary information about a single chop run associated with a specific class and
 * a specific chop type. This is really a value object used for transmitting and
 * storing information about a run after a run completes rather than to query the
 * run while it is RUNNING.
 *
 * Feel free to stuff any kind of cumulative summary information into this entity.
 * It might be nice to include some percentile information as well.
 */
public class Summary implements ISummary {
    private final long runtime;
    private long iterations;
    private String testName;
    private String chopType;
    private int threadCount;
    private int runnerCount;
    private long delayBetweenCalls;
    private long duration;
    private final int runNumber;
    private long minTime;
    private long maxTime;
    private long meanTime;
    private long failures;
    private StatsSnapshot statsSnapshot;


    public Summary( int runNumber ) {
        this.runNumber = runNumber;
        this.runtime = System.currentTimeMillis();
    }


    @Override
    @JsonProperty
    public long getIterations() {
        return iterations;
    }


    @Override
    @SuppressWarnings("UnusedDeclaration")
    public void setIterations( long iterations ) {
        this.iterations = iterations;
    }


    @Override
    @JsonProperty
    public int getThreadCount() {
        return threadCount;
    }


    @Override
    @SuppressWarnings("UnusedDeclaration")
    public void setThreadCount( int threadCount ) {
        this.threadCount = threadCount;
    }


    @Override
    @JsonProperty
    public int getRunnerCount() {
        return runnerCount;
    }


    @Override
    @SuppressWarnings("UnusedDeclaration")
    public void setRunnerCount( int runnerCount ) {
        this.runnerCount = runnerCount;
    }


    @Override
    @JsonProperty
    public long getDelayBetweenCalls() {
        return delayBetweenCalls;
    }


    @Override
    @SuppressWarnings("UnusedDeclaration")
    public void setDelayBetweenCalls( long delayBetweenCalls ) {
        this.delayBetweenCalls = delayBetweenCalls;
    }


    @Override
    @JsonProperty
    public long getRuntime() {
        return runtime;
    }


    @Override
    @JsonProperty
    public long getDuration() {
        return duration;
    }


    @Override
    @SuppressWarnings("UnusedDeclaration")
    public void setDuration( long duration ) {
        this.duration = duration;
    }


    @Override
    @JsonProperty
    public StatsSnapshot getStatsSnapshot() {
        return statsSnapshot;
    }


    @Override
    @JsonProperty
    public int getRunNumber() {
        return runNumber;
    }


    @Override
    @JsonProperty
    public String getTestName() {
        return testName;
    }


    @Override
    public void setTestName( final String testName ) {
        this.testName = testName;
    }


    @Override
    @JsonProperty
    public String getChopType() {
        return chopType;
    }


    @Override
    public void setChopType( final String chopType ) {
        this.chopType = chopType;
    }


    @Override
    @JsonProperty
    public long getMinTime() {
        return minTime;
    }


    @Override
    public void setMinTime( final long minTime ) {
        this.minTime = minTime;
    }


    @Override
    @JsonProperty
    public long getMaxTime() {
        return maxTime;
    }


    @Override
    public void setMaxTime( final long maxTime ) {
        this.maxTime = maxTime;
    }


    @Override
    @JsonProperty
    public long getMeanTime() {
        return meanTime;
    }


    @Override
    public void setMeanTime( final long meanTime ) {
        this.meanTime = meanTime;
    }
}
