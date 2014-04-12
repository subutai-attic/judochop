/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.chop.web

import org.apache.commons.lang.StringUtils
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.data.NoteService
import org.chop.service.data.PointType
import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric
import org.chop.service.store.ResultStore
import org.chop.web.util.Format
import org.chop.web.util.FormatRunner

class RunController {

    def index() {

        List<Map> jsonList = ResultStore.get(session.className, params.commitId, params.runNumber)

        int percentile = StringUtils.isEmpty(params.percentile) ? 100 : Integer.parseInt(params.percentile)
        double percentileValue = getPercentile(jsonList, percentile)

        PointType pointType = StringUtils.isEmpty(params.pointFilter) ? PointType.ALL : (params.pointFilter as PointType)

        String series = ""

        jsonList.each { json ->
            if (series != "") {
                series += ","
            }

            series += FormatRunner.format(json.runner, json.runResults, percentileValue, pointType)
        }

        series += "," + FormatRunner.format("AVG", getMainValues(jsonList, percentileValue), percentileValue, pointType)

        String note = NoteService.get(params.commitId, params.runNumber)

        render(view: "/run", model: [series: series, note: note])
    }

    private static double getPercentile(List<Map> jsonList, int percentile) {

        List<Double> listArr = []

        jsonList.each { runnerJson ->
            runnerJson.runResults.each { json ->
                listArr.add( json.runTime )
            }
        }

        double[] arr = listArr.toArray()
        return new DescriptiveStatistics(arr).getPercentile(percentile)
    }

    private List<Map> getMainValues(List<Map> jsonList, double percentileValue) {

        List<List<Map>> matrix = []

        jsonList.each { runner ->
            matrix.add(runner.runResults)
        }

        if (matrix.size() == 0) {
            return []
        }

        int rows = matrix.size()
        int columns = matrix[0].size()
        List<Map> resultList = []

        for (int j = 0; j < columns; j++) {
            Map json = [
                    "runTime": null,
                    "ignoreCount": 0,
                    "failureCount": 0,
                    "failures": [ ]
            ]

            int rowsAdded = 0

            for (int i = 0; i < rows; i++) {
                if (matrix[i][j].runTime <= percentileValue) {

                    if (json.runTime == null) {
                        json.runTime = 0
                    }

                    json.runTime += matrix[i][j].runTime
                    json.ignoreCount += matrix[i][j].ignoreCount
                    json.failureCount += matrix[i][j].failureCount
                    rowsAdded++
                }
            }

            if (rowsAdded > 0) {
                json.runTime = json.runTime / rowsAdded
            }

            resultList.add(json)
        }

        return resultList
    }
}
