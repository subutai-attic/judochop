package org.chop.service.data

import org.chop.service.value.*

class CommitCalc {

    private String className
    private String metric

    CommitCalc(String className, String metric) {
        this.className = className
        this.metric = metric
    }

    Map<String, List<Value>> get() {

        // <commitId, list>
        Map<String, List<Value>> map = new LinkedHashMap()

        Storage.getData().each { commitId, jsonList ->
            map.put(commitId, collect(jsonList) )
        }

        return map
    }

    private List<Value> collect(List<Map<String, String>> jsonList) {

        // <runNumber, value>
        Map<String, Value> valueMap = new HashMap()

        List list = jsonList.findAll { it.testName == className }

        list.each { json ->
            putValue(valueMap, json)
        }

        return valueMap.values().collect()
    }

    private void putValue(Map<String, Value> valueMap, Map<String, String> json) {
        Value value = valueMap.get(json.runNumber,  createValue(metric) )
        value.merge(json)
    }

    private static Value createValue(String metric) {

        Value value

        if (metric == "minTime") {
            value = new MinValue()
        } else if (metric == "maxTime") {
            value = new MaxValue()
        } else if (metric == "actualTime") {
            value = new ActualValue()
        } else {
            value = new AvgValue()
        }

        return value
    }
}
