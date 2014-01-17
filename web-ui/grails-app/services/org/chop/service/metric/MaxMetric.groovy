package org.chop.service.metric

class MaxMetric extends Metric {

    protected void calc(Map<String, String> json) {
        if (json.maxTime > value) {
            value = json.maxTime
        }
    }
}
