package org.safehaus.chop.webapp.service.metric;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class Metric {

    private double value;
    private int count;

    private int failures;

    public void merge(Run run) {
        value += run.getAvgTime();
        count++;

        failures += run.getFailures();
    }

    public void merge(Metric metric) {
        value += metric.getValue();
        count++;

        failures += metric.getFailures();
    }

    public int getFailures() {
        return failures;
    }

    public double getValue() {
        return value / count;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", getValue())
                .append("failures", failures)
                .toString();
    }
}
