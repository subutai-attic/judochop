package org.safehaus.chop.webapp.service.chart.value;

public class Value {

    protected double value;
    protected long failures;
    protected long ignores;

    public double getValue() {
        return value;
    }

    public long getFailures() {
        return failures;
    }
}
