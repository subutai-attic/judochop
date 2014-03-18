package org.safehaus.chop.webapp.service.metric;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class AvgMetric extends Metric {

    private int count;

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
    }

    @Override
    public double getValue() {
        return value / count;
    }
}
