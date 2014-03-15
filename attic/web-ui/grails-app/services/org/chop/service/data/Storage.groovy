package org.chop.service.data

import org.chop.service.metric.Metric
import org.chop.service.metric.MetricFactory
import org.chop.service.metric.MetricType

class Storage {

    // <commitId, List<json>>
    private static final Map<String, List<Map<String, Object>> > DATA = new LinkedHashMap()

    static void add(String commitId, Map<String, String> json) {
        List<Map<String, Object>> list = DATA.get(commitId, new ArrayList<Map<String, Object>>() )
        list.add(json)
    }

    static Map getData() {
        return DATA
    }

    static Set<String> getClassNames() {

        Set<String> names = new HashSet()

        DATA.each { commitIt, jsonList ->
            jsonList.each { json ->
                names.add(json.testName)
            }
        }

        return names
    }

    static List<Map> findByCommitAndClass(String commitIdParam, String className) {

        List<Map> jsonList = DATA.values().collect().flatten()

        return jsonList.findAll { json ->
            json.commitId == commitIdParam && json.testName == className
        }
    }

/*    static clear() {
        DATA.clear()
    }*/
}
