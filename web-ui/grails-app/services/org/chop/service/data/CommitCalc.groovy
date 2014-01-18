package org.chop.service.data

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.metric.*

class CommitCalc {

    private String className
    private MetricType metricType
    private int percentile

    CommitCalc(String className, MetricType metricType, int percentile) {
        this.className = className
        this.metricType = metricType
        this.percentile = percentile
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

        return filterByPercentile(resultList)
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
