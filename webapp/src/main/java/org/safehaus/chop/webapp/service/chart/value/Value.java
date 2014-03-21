package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.simple.JSONObject;
import org.safehaus.chop.api.RunResult;

public class Value {

    protected double value;
    protected long failures;
    protected long ignores;

    protected JSONObject properties = new JSONObject();

    public Value(RunResult runResult) {
        this( runResult.getRunTime(), runResult.getFailureCount(), runResult.getIgnoreCount() );
    }

    public Value(double value, long failures, long ignores) {
        this.value = value;
        this.failures = failures;
        this.ignores = ignores;
    }

    public double getValue() {
        return value;
    }

    public long getFailures() {
        return failures;
    }

    public JSONObject getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", getValue())
                .toString();
    }
}
