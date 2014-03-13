package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Summary;

public class BasicSummary implements Summary {

    private String runId;
    private String chopType;
    private long iterations;
    private long totalTestsRun;
    private int threads;
    private long delay;
    private long time;
    private long actualTime;
    private long minTime;
    private long maxTime;
    private long meanTime;
    private long failures;
    private long ignores;
    private long startTime;
    private long stopTime;
    private boolean saturate;

    public BasicSummary(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("runId", runId)
                .append("chopType", chopType)
                .append("iterations", iterations)
                .append("totalTestsRun", totalTestsRun)
                .toString();
    }

    public long getIterations() {
        return iterations;
    }

    public long getTotalTestsRun() {
        return totalTestsRun;
    }

    public String getChopType() {
        return chopType;
    }

    public int getThreads() {
        return threads;
    }

    public long getDelay() {
        return delay;
    }

    public long getTime() {
        return time;
    }

    public long getActualTime() {
        return actualTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getAvgTime() {
        return meanTime;
    }

    public long getFailures() {
        return failures;
    }

    public long getIgnores() {
        return ignores;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public boolean getSaturate() {
        return saturate;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    public void setTotalTestsRun(long totalTestsRun) {
        this.totalTestsRun = totalTestsRun;
    }

    public void setChopType(String chopType) {
        this.chopType = chopType;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setActualTime(long actualTime) {
        this.actualTime = actualTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public void setMeanTime(long meanTime) {
        this.meanTime = meanTime;
    }

    public void setFailures(long failures) {
        this.failures = failures;
    }

    public void setIgnores(long ignores) {
        this.ignores = ignores;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void setSaturate(boolean saturate) {
        this.saturate = saturate;
    }
}
