package org.chop.service.data

class Storage {

    // <commitId, List<json>>
    private static final Map<String, List<Map<String, String>> > DATA = new LinkedHashMap()

    static void add(String commitId, Map<String, String> json) {
        List<Map<String, String>> list = DATA.get(commitId, new ArrayList<Map<String, String>>() )
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

    static clear() {
        DATA.clear()
    }
}
