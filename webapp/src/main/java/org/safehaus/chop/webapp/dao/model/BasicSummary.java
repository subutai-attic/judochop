package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Summary;

public class BasicSummary implements Summary {

    private String id;
    private String commitId;
    private String runner;
    private int runNumber;
    private String testName;
    private long iterations;
    private long totalTestsRun;
    private String chopType;
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

    public BasicSummary(String commitId, String runner, int runNumber, String testName) {
        id = createId(commitId, runner, runNumber, testName);
        this.commitId = commitId;
        this.runner = runner;
        this.runNumber = runNumber;
        this.testName = testName;
    }

    public String getId() {
        return id;
    }

    private static String createId(String commitId, String runner, int runNumber, String testName) {
        return "" + new HashCodeBuilder()
                .append(commitId)
                .append(runner)
                .append(runNumber)
                .append(testName)
                .toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("commitId", commitId)
                .append("runner", runner)
                .append("runNumber", runNumber)
                .append("testName", testName)
                .toString();
    }

    public String getCommitId() {
        return commitId;
    }

    public String getRunner() {
        return runner;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public long getIterations() {
        return iterations;
    }

    public long getTotalTestsRun() {
        return totalTestsRun;
    }

    public String getTestName() {
        return testName;
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

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public void setRunner(String runner) {
        this.runner = runner;
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    public void setTotalTestsRun(long totalTestsRun) {
        this.totalTestsRun = totalTestsRun;
    }

    public void setTestName(String testName) {
        this.testName = testName;
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
