package org.chop.service.data

import org.chop.service.metric.Metric
import org.chop.service.metric.MetricFactory
import org.chop.service.metric.MetricType

class Group {

    static Map<Integer, Metric> byRun(List<Map> jsonList, MetricType metricType) {

        Map<Integer, Metric> resultMap = new HashMap()

        jsonList.each { json ->
            put(resultMap, json, metricType)
        }

        return resultMap
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
