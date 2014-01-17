package org.chop.service.data

import org.chop.service.metric.*

class CommitCalc {

    private String className
    private MetricType metricType

    CommitCalc(String className, MetricType metricType) {
        this.className = className
        this.metricType = metricType
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

        return valueMap.values().collect()
    }

    private void putValue(Map<String, Metric> valueMap, Map<String, String> json) {

        Metric value = valueMap.get(json.runNumber)

        if (value == null) {
            value = MetricFactory.create(metricType, json)
            valueMap.put(json.runNumber, value)
        }

        value.merge(json)
    }

}
