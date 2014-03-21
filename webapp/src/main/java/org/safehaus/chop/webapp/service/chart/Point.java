package org.safehaus.chop.webapp.service.chart;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.simple.JSONObject;

public class Point {

    private int x;
    private double y;
    private long failures;
    private JSONObject properties;

    public Point(int x, double y, long failures, JSONObject properties) {
        this.x = x;
        this.y = y;
        this.failures = failures;
        this.properties = properties;
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
