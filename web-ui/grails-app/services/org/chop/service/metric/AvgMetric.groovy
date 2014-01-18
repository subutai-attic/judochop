package org.chop.service.metric

class AvgMetric extends Metric {

    private int count = 0

    protected void calc(Map<String, String> json) {
        count++
        data.value = data.value + json.meanTime
    }

    @Override
    double getValue() {
        return data.value / count
    }
}
