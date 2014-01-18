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

    private static String formatPoint(int x, Metric value) {

        String pointColor = 'white'
        int markerRadius = 4

        if (value.data.ignores > 0) {
            pointColor = 'yellow'
        }

        if (value.data.failures > 0) {
            pointColor = 'red'
            markerRadius = 6
        }

        if (value instanceof AggregatedMetric) {
            markerRadius = 10
        }

        """{
                x: $x,
                y: ${value.data.value},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                },
                info: ${value.toJson()}
        }"""
    }
}
