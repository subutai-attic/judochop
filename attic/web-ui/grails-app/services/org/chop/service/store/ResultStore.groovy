package org.chop.service.store

class ResultStore {

    static final List<Map> DATA = []

    static List<Map> get(String testClass, String commitId, String runNumber) {
        return DATA.findAll { json ->
            json.testClass == testClass && json.commitId == commitId && json.runNumber == runNumber
        }
    }

}
