package org.safehaus.chop.webapp.service.chart;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Point {

    private int x;
    private double y;
    private long failures;

    public Point(int x, double y, long failures) {
        this.x = x;
        this.y = y;
        this.failures = failures;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("x", x)
                .append("y", y)
                .toString();
    }
}
