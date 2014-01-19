package org.chop.service.data

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.metric.Metric
import org.chop.service.metric.MetricFactory
import org.chop.service.metric.MetricType

class Group {

    static Map<Integer, Metric> byRun(List<Map> jsonList, MetricType metricType, int percentile) {

        Map<Integer, Metric> resultMap = new HashMap()

        jsonList.each { json ->
            put(resultMap, json, metricType)
        }

        return filterByPercentile(resultMap, percentile)
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
