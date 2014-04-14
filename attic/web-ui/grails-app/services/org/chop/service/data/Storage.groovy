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
