package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.RunResult;

public class BasicRunResult implements RunResult {

    private int runCount;
    private int runTime;
    private int ignoreCount;
    private int failureCount;

    public BasicRunResult(int runCount, int runTime, int ignoreCount, int failureCount) {
        this.runCount = runCount;
        this.runTime = runTime;
        this.ignoreCount = ignoreCount;
        this.failureCount = failureCount;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("runCount", runCount)
                .append("runTime", runTime)
                .append("ignoreCount", ignoreCount)
                .append("failureCount", failureCount)
                .toString();
    }

    @Override
    public int getRunCount() {
        return runCount;
    }

    @Override
    public int getRunTime() {
        return runTime;
    }

    @Override
    public int getIgnoreCount() {
        return ignoreCount;
    }

    @Override
    public int getFailureCount() {
        return failureCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public void setIgnoreCount(int ignoreCount) {
        this.ignoreCount = ignoreCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }
}
