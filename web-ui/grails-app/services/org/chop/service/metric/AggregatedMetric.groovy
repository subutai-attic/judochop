package org.chop.service.metric

class AggregatedMetric extends Metric {

    private int count = 0

    protected void calc(Map<String, String> json) { }

    void add(Metric metric) {

        count++
        value = (value + metric.value) / count

        chopType = metric.chopType
        runNumber = "ALL"
        iterations += metric.iterations
        totalTestsRun += metric.totalTestsRun
        failures += metric.failures
        ignores += metric.ignores
    }

}
