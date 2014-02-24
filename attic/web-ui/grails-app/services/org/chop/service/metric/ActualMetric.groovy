package org.chop.service.metric

class ActualMetric extends Metric {

    protected void calc(Map<String, String> json) {
        data.value += json.actualTime
    }
}
