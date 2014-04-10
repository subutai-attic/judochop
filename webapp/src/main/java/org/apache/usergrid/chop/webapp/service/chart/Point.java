package org.apache.usergrid.chop.webapp.service.chart;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.JSONObject;
import org.apache.usergrid.chop.webapp.service.chart.value.Value;

public class Point {

    private int x;
    private double y;
    private long failures;
    private long ignores;
    private JSONObject properties;

    public Point(int x, Value value) {
        this.x = x;
        this.y = value.getValue();
        this.failures = value.getFailures();
        this.ignores = value.getIgnores();
        this.properties = value.toJson();
    }

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public long getFailures() {
        return failures;
    }

    public long getIgnores() {
        return ignores;
    }

    public JSONObject getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("x", x)
                .append("y", y)
                .toString();
    }
}
