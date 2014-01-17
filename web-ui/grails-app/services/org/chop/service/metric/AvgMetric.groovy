package org.chop.service.metric

class AvgMetric extends Metric {

    private int count = 0

    protected void calc(Map<String, String> json) {
        count++
        this.value = (value + json.meanTime) / count
    }
}
