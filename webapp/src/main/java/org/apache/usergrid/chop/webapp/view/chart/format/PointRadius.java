package org.apache.usergrid.chop.webapp.view.chart.format;

import org.apache.usergrid.chop.webapp.service.chart.Point;

public class PointRadius {

    private int default_ = 4;
    private int maxFailures = 1000;
    private int maxValue = 15;
    private int avgFailures = 500;
    private int avgValue = 10;
    private int minFailures = 100;
    private int minValue = 7;

    public PointRadius() {
        this(1000, 15, 500, 10, 100, 7);
    }

    public PointRadius(int maxFailures, int maxValue, int avgFailures, int avgValue, int minFailures, int minValue) {
        this.maxFailures = maxFailures;
        this.maxValue = maxValue;
        this.avgFailures = avgFailures;
        this.avgValue = avgValue;
        this.minFailures = minFailures;
        this.minValue = minValue;
    }

    public int get(Point point) {

        int r = default_;

        if (point.getFailures() > maxFailures) {
            r = maxValue;
        } else if (point.getFailures() > avgFailures) {
            r = avgValue;
        } else if (point.getFailures() > minFailures) {
            r = minValue;
        }

        return r;
    }

}
