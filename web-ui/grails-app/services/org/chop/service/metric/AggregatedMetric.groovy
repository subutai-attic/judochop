package org.chop.service.metric

class AggregatedMetric extends Metric {

    private int count = 0

    protected void calc(Map<String, String> json) { }

    void add(Metric metric) {

        collect(metric.data)

        count++
        data.value = (data.value + metric.data.value) / count

        data.runNumber = "ALL"
    }
}
