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

import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric

class FormatCommit {

    static String format(Map<Integer, Metric> runMap) {

        List<String> data = runMap.collect { runNumber, metric ->
            formatPoint(runNumber, metric)
        }

        String s = """{
            name: '',
            dashStyle: 'shortdot',
            lineColor: 'blue',
            data: ${data}
        }"""

        return s
    }

    private static String formatPoint(Integer runNumber, Metric metric) {

        String pointColor = 'white'
        int markerRadius = 4

        if (metric.data.ignores > 0) {
            pointColor = 'yellow'
        }

        if (metric.data.failures > 0) {
            pointColor = 'red'

            if (metric.data.failures > 100) {
                markerRadius = 10
            }

            if (metric.data.failures > 1000) {
                markerRadius = 15
            }
        }

        """{
                x: $runNumber,
                y: ${metric.getValue()},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                },
                info: ${metric.toJson()}
        }"""
    }
}
