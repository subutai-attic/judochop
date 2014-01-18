package org.chop.web

import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric
import org.chop.service.store.ResultStore
import org.chop.web.util.Format
import org.chop.web.util.FormatRunner

class RunController {

    def index() {

        List<Map> jsonList = ResultStore.get(session.className, params.commitId, params.runNumber)

        String series = ""

        jsonList.each { json ->
            if (series != "") {
                series += ","
            }

            series += FormatRunner.format(json.runner, json.runResults)
        }

        series += "," + FormatRunner.format("AVG", getMainValues(jsonList) )

        render(view: "/run", model: [series: series])
    }

    private List<Metric> getMainValues(List<Map> jsonList) {

        List<List<Map>> matrix = []

        jsonList.each { runner ->
            matrix.add(runner.runResults)
        }

        int rows = matrix.size()
        int columns = matrix[0].size()
        List<Map> resultList = []

        for (int j = 0; j < columns; j++) {
            Map json = [
                    "runTime": 0,
                    "ignoreCount": 0,
                    "failureCount": 0,
                    "failures": [ ]
            ]

            for (int i = 0; i < rows; i++) {
                json.runTime += matrix[i][j].runTime
                json.ignoreCount += matrix[i][j].ignoreCount
                json.failureCount += matrix[i][j].failureCount
            }

            json.runTime = json.runTime / rows

            resultList.add(json)
        }

        return resultList
    }
}
