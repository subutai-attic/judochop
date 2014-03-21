package org.safehaus.chop.webapp.service.chart.value;

public class ValueFactory {

    public static Value get(String metricType) {

        Value value;

        if ("Avg Time".equals(metricType)) {
            value = new AvgValue();
        } else if ("Min Time".equals(metricType)) {
            value = new MinValue();
        } else if ("Max Time".equals(metricType)) {
            value = new MaxValue();
        } else {
            value = new ActualValue();
        }

        return value;
    }
}
