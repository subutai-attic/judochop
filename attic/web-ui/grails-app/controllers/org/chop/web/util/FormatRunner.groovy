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
package org.chop.web.util

import groovy.json.JsonBuilder
import org.chop.service.data.PointType

class FormatRunner {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    static String format(String runner, List<Map> runResults, double percentileValue, PointType pointType) {

        List<String> data = []

        int i = 0
        runResults.each { json ->
            if (i < 500) {
                data.add( formatPoint(json, percentileValue, pointType) )
            }
            i++
        }

        String s = """{
            name: '${runner}',
            data: ${data}
        }"""

        return s
    }

    private static String formatPoint(Map json, double percentileValue, PointType pointType) {

        String pointColor = 'white'
        int markerRadius = 4

        if (json.ignoreCount > 0) {
            pointColor = 'yellow'
        }

        if (json.failureCount > 0) {
            pointColor = 'red'

            if (json.failureCount > 5) {
                markerRadius = 7
            }

            if (json.failureCount > 10) {
                markerRadius = 10
            }

            if (json.failureCount > 20) {
                markerRadius = 15
            }
        }

        BUILDER(json)
        String strJson = BUILDER.toString()

        Double y = json.runTime <= percentileValue ? json.runTime : null

        int failures = json.failureCount

        if (y != null) {
            y = null
            if (pointType == PointType.ALL
                    || (pointType == PointType.FAILED && failures > 0)
                    || (pointType == PointType.SUCCESS && failures == 0)
            ) {
                y = json.runTime
            }
        }

        """{
                y: ${y},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                },
                info: ${strJson}
        }"""
    }
}
