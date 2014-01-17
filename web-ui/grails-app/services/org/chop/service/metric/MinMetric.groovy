package org.chop.service.metric

class MinMetric extends Metric {

    MinMetric() {
        value = Double.MAX_VALUE
    }

    protected void calc(Map<String, String> json) {
        if (json.minTime < value) {
            value = json.minTime
        }
    }
}
