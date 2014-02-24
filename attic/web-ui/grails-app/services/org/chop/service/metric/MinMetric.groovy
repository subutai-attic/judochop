package org.chop.service.metric

class MinMetric extends Metric {

    MinMetric() {
        data.value = Double.MAX_VALUE
    }

    protected void calc(Map<String, String> json) {
        if (json.minTime < data.value) {
            data.value = json.minTime
        }
    }
}
