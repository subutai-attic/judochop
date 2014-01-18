package org.chop.service.metric

import groovy.json.JsonBuilder

abstract class Metric {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    Map<String, Object> data = [
        chopType: "",
        commitId: "",
        runNumber: 0,
        totalTestsRun: 0,
        iterations: 0,
        failures: 0,
        ignores: 0,
        value: 0
    ]

    protected abstract void calc(Map<String, String> json)

    void merge(Map<String, String> json) {
        collect(json)
        calc(json)
    }

    private void collect(Map<String, String> json) {

        data.chopType = json.chopType
        data.commitId = json.commitId
        data.runNumber = json.runNumber
        data.totalTestsRun += json.totalTestsRun
        data.failures += json.failures
        data.ignores += json.ignores

        if (json.containsKey("threads") && json.containsKey("iterations")) {
            data.iterations += json.threads * json.iterations
        }

    }

    String toJson() {

        Map tempMap = [:]

        tempMap.putAll(data)
        tempMap.value = getValue()

        BUILDER(tempMap)

        return BUILDER.toString()
    }

    double getValue() {
        return data.value
    }
}
