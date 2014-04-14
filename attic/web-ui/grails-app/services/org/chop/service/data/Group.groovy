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
package org.chop.service.data

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.metric.Metric
import org.chop.service.metric.MetricFactory
import org.chop.service.metric.MetricType

class Group {

    static Map<Integer, Metric> byRun(List<Map> jsonList, MetricType metricType, int percentile, PointType pointType) {

        Map<Integer, Metric> resultMap = new HashMap()

        jsonList.each { json ->
            put(resultMap, json, metricType)
        }

        resultMap = filterByPercentile(resultMap, percentile)
        resultMap = filterByPointType(resultMap, pointType)

        return resultMap
    }

    private static Map<Integer, Metric> filterByPointType(Map<Integer, Metric> map, PointType pointType) {

        Map<Integer, Metric> resultMap = new HashMap()

        map.each { runNumber, metric ->

            int failures = metric.data.failures

            if (pointType == PointType.ALL
                    || (pointType == PointType.FAILED && failures > 0)
                    || (pointType == PointType.SUCCESS && failures == 0)
            ) {
                resultMap.put(runNumber, metric)
            }
        }

        return resultMap
    }

    private static Map<Integer, Metric> filterByPercentile(Map<Integer, Metric> resultMap, int percentile) {

        List<Double> listArr = []

        resultMap.each { runNumber, metric ->
            listArr.add( metric.getValue() )
        }

        double[] arr = listArr.toArray()
        double p = new DescriptiveStatistics(arr).getPercentile(percentile)

        return resultMap.findAll { runNumber, metric ->
            metric.getValue() <= p
        }
    }

    private static void put(Map<Integer, Metric> resultMap, Map<String, String> json, MetricType metricType) {

        Metric metric = resultMap.get(json.runNumber)

        if (metric == null) {
            metric = MetricFactory.create(metricType, json)
            resultMap.put(json.runNumber, metric)
        }

        metric.merge(json)
    }

}
