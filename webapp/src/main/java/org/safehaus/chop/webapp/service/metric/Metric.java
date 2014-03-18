package org.safehaus.chop.webapp.service.metric;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

import java.util.Map;

public abstract class Metric {

    protected double value;
    protected int failures;
    protected String chopType;
    protected int runNumber;
    protected String commitId;

    protected abstract void calc(Run run);

    public void merge(Run run) {
        calc(run);

        failures += run.getFailures();
        chopType = run.getChopType();
        runNumber = run.getRunNumber();
        commitId = run.getCommitId();
    }

    public double getValue() {
        return value;
    }

    public int getFailures() {
        return failures;
    }

    public String getChopType() {
        return chopType;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public String getCommitId() {
        return commitId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", getValue())
                .append("failures", failures)
                .toString();
    }
}
