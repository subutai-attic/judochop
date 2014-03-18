package org.safehaus.chop.webapp.service.metric;

import org.safehaus.chop.api.Run;

public class AvgMetric extends Metric {

    private int count;
    private boolean mainAvg;

    public AvgMetric(boolean mainAvg) {
        this.mainAvg = mainAvg;
    }

    @Override
    public void calc(Run run) {
        value += run.getAvgTime();
        count++;
    }

    public void merge(Metric metric) {
        value += metric.getValue();
        count++;

        failures += metric.getFailures();
        commitId = metric.getCommitId();
        chopType = metric.getChopType();
        totalTestsRun += metric.getTotalTestsRun();
    }

    @Override
    public double getValue() {
        return value / count;
    }

    @Override
    public String getRunNumberString() {
        return mainAvg ? "N/A" : super.getRunNumberString();
    }

    @Override
    public String getRunnersString() {
        return mainAvg ? "N/A" : super.getRunnersString();
    }

    @Override
    public String getIterationsString() {
        return mainAvg ? "N/A" : super.getIterationsString();
    }
}
