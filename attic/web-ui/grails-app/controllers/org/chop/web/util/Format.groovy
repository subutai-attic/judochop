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

class Format {

    static String formatCommit(int x, String commitId, List<Metric> metricList) {

        List<String> data = metricList.collect {
            formatPoint(x, it)
        }

        return formatSeries(commitId, data)
    }

    static String formatValues(List<Metric> metricList) {

        int i = 0
        List<String> data = []

        metricList.each {
            data.add( formatPoint(i, it) )
            i++
        }

        return formatSeries('main', data)
    }

    private static String formatSeries(String name, List data) {
        return """{
            name: '$name',
            dashStyle: 'shortdot',
            lineColor: 'blue',
            data: ${data}
        }"""
    }

    private static String formatPoint(int x, Metric metric) {

        String pointColor = 'white'
        int markerRadius = 4

        if (metric.data.ignores > 0) {
            pointColor = 'yellow'
        }

        if (metric.data.failures > 0) {
            pointColor = 'red'

            if (metric.data.failures > 100) {
                markerRadius = 7
            }

            if (metric.data.failures > 500) {
                markerRadius = 10
            }

            if (metric.data.failures > 1000) {
                markerRadius = 15
            }
        }

        String main = ''

        if (metric instanceof AggregatedMetric) {
            main = ", symbol: 'circle', lineColor: 'blue', lineWidth: '2'"
        }

        """{
                x: $x,
                y: ${metric.getValue()},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                    ${main}
                },
                info: ${metric.toJson()}
        }"""
    }
}
