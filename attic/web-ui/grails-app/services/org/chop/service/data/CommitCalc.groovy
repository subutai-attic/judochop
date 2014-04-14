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
import org.chop.service.metric.*

class CommitCalc {

    private String className
    private MetricType metricType
    private int percentile
    PointType pointType

    CommitCalc(String className, MetricType metricType, int percentile, PointType pointType) {
        this.className = className
        this.metricType = metricType
        this.percentile = percentile
        this.pointType = pointType
    }

    Map<String, List<Metric>> get() {

        // <commitId, list>
        Map<String, List<Metric>> map = new LinkedHashMap()
        Map data = Storage.getData()

        data.each { commitId, jsonList ->
            map.put(commitId, collectMetrics(jsonList, className) )
        }

        return map
    }

    private List<Metric> collectMetrics(List<Map<String, String>> jsonList, String className) {

        // <runNumber, value>
        Map<String, Metric> valueMap = new HashMap()

        jsonList.each { json ->
            if (json.testName == className) {
                putValue(valueMap, json)
            }
        }

        List<Metric> resultList = valueMap.values().collect()
        resultList = filterByPercentile(resultList)
        resultList = filterByPointType(resultList)

        return resultList
    }

    private List<Metric> filterByPointType(List<Metric> list) {

        List<Metric> resultList = []

        list.each { metric ->

            int failures = metric.data.failures

            if (pointType == PointType.ALL
                    || (pointType == PointType.FAILED && failures > 0)
                    || (pointType == PointType.SUCCESS && failures == 0)
            ) {
                resultList.add(metric)
            }
        }

        return resultList
    }

    private List<Metric> filterByPercentile(List<Metric> list) {

        List<Double> listArr = []

        list.each { metric ->
            listArr.add( metric.getValue() )
        }

        double[] arr = listArr.toArray()
        double p = new DescriptiveStatistics(arr).getPercentile(percentile)

        return list.findAll { metric ->
            metric.getValue() <= p
        }
    }

    private void putValue(Map<String, Metric> valueMap, Map<String, String> json) {

        Metric metric = valueMap.get(json.runNumber)

        if (metric == null) {
            metric = MetricFactory.create(metricType, json)
            valueMap.put(json.runNumber, metric)
        }

        metric.merge(json)
    }

}
