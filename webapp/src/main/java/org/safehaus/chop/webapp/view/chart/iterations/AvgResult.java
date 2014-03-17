package org.safehaus.chop.webapp.view.chart.iterations;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

public class AvgResult implements RunResult {

    private String id;
    private String runId;
    private int runCount;
    private int runTime;
    private int ignoreCount;
    private int failureCount;
    private String failures = "";

    private int count;

    public void merge(RunResult runResult) {
        if (runResult == null) {
            return;
        }

        runTime += runResult.getRunTime();
        count++;

        failureCount += runResult.getFailureCount();
        ignoreCount += runResult.getIgnoreCount();
    }

    @Override
    public int getRunTime() {
        return count == 0 ? -1 : runTime / count;
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

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("runId", runId)
                .append("runCount", runCount)
                .append("runTime", getRunTime())
                .append("ignoreCount", ignoreCount)
                .append("failureCount", failureCount)
                .append("failures", failures)
                .toString();
    }
}
