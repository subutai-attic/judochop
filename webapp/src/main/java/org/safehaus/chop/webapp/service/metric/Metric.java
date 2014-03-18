package org.safehaus.chop.webapp.service.metric;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

import java.util.Map;

public abstract class Metric {

    protected double value;
    protected int failures;
    protected int ignores;
    protected String chopType;
    protected int runNumber;
    protected String commitId;
    protected int runners;
    protected int totalTestsRun;
    protected int iterations;

    protected abstract void calc(Run run);

    public void merge(Run run) {
        calc(run);

        failures += run.getFailures();
        chopType = run.getChopType();
        runNumber = run.getRunNumber();
        commitId = run.getCommitId();
        runners++;
        totalTestsRun += run.getTotalTestsRun();
        iterations += run.getThreads() * run.getIterations();
        ignores += run.getIgnores();
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

    public String getRunNumberString() {
        return String.valueOf( runNumber );
    }

    public String getRunnersString() {
        return String.valueOf( runners );
    }

    public String getCommitId() {
        return commitId;
    }

    public int getTotalTestsRun() {
        return totalTestsRun;
    }

    public String getIterationsString() {
        return String.valueOf( iterations );
    }

    public int getIgnores() {
        return ignores;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", getValue())
                .append("failures", failures)
                .toString();
    }
}
