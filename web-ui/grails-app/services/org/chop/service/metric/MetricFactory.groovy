package org.chop.service.metric

class MetricFactory {

    static Metric create(MetricType type, Map<String, String> json) {

        Metric value

        if (type == MetricType.MIN) {
            value = new MinMetric()
        } else if (type == MetricType.MAX) {
            value = new MaxMetric()
        } else if (type == MetricType.ACTUAL) {
            value = new ActualMetric()
        } else {
            value = new AvgMetric()
        }

        return value
    }
}
