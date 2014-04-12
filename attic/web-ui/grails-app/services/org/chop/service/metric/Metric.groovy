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
package org.chop.service.metric

import groovy.json.JsonBuilder

abstract class Metric {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    Map<String, Object> data = [
        chopType: "",
        commitId: "",
        runNumber: 0,
        totalTestsRun: 0,
        iterations: 0,
        failures: 0,
        ignores: 0,
        runners: 0,
        value: 0
    ]

    protected abstract void calc(Map<String, String> json)

    void merge(Map<String, String> json) {
        collect(json)
        calc(json)
    }

    private void collect(Map<String, String> json) {

        data.chopType = json.chopType
        data.commitId = json.commitId
        data.runNumber = json.runNumber
        data.totalTestsRun += json.totalTestsRun
        data.failures += json.failures
        data.ignores += json.ignores
        data.runners++

        if (json.containsKey("threads") && json.containsKey("iterations")) {
            data.iterations += json.threads * json.iterations
        }

    }

    String toJson() {

        Map tempMap = [:]

        tempMap.putAll(data)
        tempMap.value = getValue()

        BUILDER(tempMap)

        return BUILDER.toString()
    }

    Double getValue() {
        return data.value
    }
}
