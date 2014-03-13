package org.safehaus.chop.webapp.dao.model;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Summary;

public class BasicSummary implements Summary {

    private Commit version;
    private int runNumber;
    private long iterations;
    private long totalTestsRun;
    private String testName;
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
    private boolean saturate = false;

    public BasicSummary(int runNumber, long iterations, long totalTestsRun, String testName) {
        this.runNumber = runNumber;
        this.iterations = iterations;
        this.totalTestsRun = totalTestsRun;
        this.testName = testName;
    }

    @Override
    public Commit getCommit() {
        return version;
    }

    @Override
    public int getRunNumber() {
        return runNumber;
    }

    @Override
    public long getIterations() {
        return iterations;
    }

    @Override
    public long getTotalTestsRun() {
        return totalTestsRun;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public String getChopType() {
        return null;  
    }

    @Override
    public int getThreads() {
        return 0;  
    }

    @Override
    public long getDelay() {
        return 0;  
    }

    @Override
    public long getTime() {
        return 0;  
    }

    @Override
    public long getActualTime() {
        return 0;  
    }

    @Override
    public long getMinTime() {
        return 0;  
    }

    @Override
    public long getMaxTime() {
        return 0;  
    }

    @Override
    public long getMeanTime() {
        return 0;  
    }

    @Override
    public long getFailures() {
        return 0;  
    }

    @Override
    public long getIgnores() {
        return 0;  
    }

    @Override
    public long getStartTime() {
        return 0;  
    }

    @Override
    public long getStopTime() {
        return 0;  
    }

    @Override
    public boolean getSaturate() {
        return false;  
    }
}
