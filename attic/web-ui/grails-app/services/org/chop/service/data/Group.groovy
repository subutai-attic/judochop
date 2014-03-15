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
