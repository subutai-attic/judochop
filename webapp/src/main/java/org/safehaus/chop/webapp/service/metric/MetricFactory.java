package org.safehaus.chop.webapp.service.metric;

public class MetricFactory {

//            metric = new MinMetric();
//            metric = new MaxMetric();
//            metric = new ActualMetric();

    public static Metric getMetric(String metricType) {

        Metric metric;

        if ("Avg Time".equals(metricType)) {
            metric = new AvgMetric();
        } else if ("Min Time".equals(metricType)) {
            metric = new MinMetric();
        } else if ("Max Time".equals(metricType)) {
            metric = new MaxMetric();
        } else {
            metric = new ActualMetric();
        }

        return metric;
    }

}
