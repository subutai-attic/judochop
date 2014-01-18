package org.chop.web.util

import groovy.json.JsonBuilder

class FormatRunner {

    private static final JsonBuilder BUILDER = new JsonBuilder()

    static String format(String runner, List<Map> runResults) {

        List<String> data = []

        int i = 0
        runResults.each { json ->
            if (i < 500) {
                data.add(formatPoint(json))
            }
            i++
        }

        String s = """{
            name: '${runner}',
            data: ${data}
        }"""

        return s
    }

    private static String formatPoint(Map json) {

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

        """{
                y: ${json.runTime},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                },
                info: ${strJson}
        }"""
    }
}
