package org.chop.service.metric

abstract class Metric {

    String chopType
    String runNumber = ""
    int totalTestsRun = 0
    int iterations = 0
    int failures = 0
    int ignores = 0
    double value = 0

    protected abstract void calc(Map<String, String> json)

    void merge(Map<String, String> json) {
        collect(json)
        calc(json)
    }

    private void collect(Map<String, String> json) {
        chopType = json.chopType
        runNumber = json.runNumber
        totalTestsRun += json.totalTestsRun
        iterations += json.threads * json.iterations
        failures += json.failures
        ignores += json.ignores
    }
}
