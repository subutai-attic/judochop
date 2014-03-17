package org.safehaus.chop.webapp.view.chart.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

public class AvgResult implements RunResult {

    private String id;
    private String runId;
    private int runCount;
    private int runTime;
    private int ignoreCount;
    private int failureCount;
    private String failures;

    private int count;

    public void merge(RunResult runResult) {
        runTime += runResult.getRunTime();
        count++;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public int getRunCount() {
        return runCount;
    }

    @Override
    public int getRunTime() {
        return runTime / count;
    }

    @Override
    public int getIgnoreCount() {
        return ignoreCount;
    }

    @Override
    public int getFailureCount() {
        return failureCount;
    }

    @Override
    public String getFailures() {
        return failures;
    }
}
