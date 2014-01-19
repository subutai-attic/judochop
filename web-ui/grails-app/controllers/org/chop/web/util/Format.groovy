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
            main = ", symbol: 'circle', lineColor: 'yellow', lineWidth: '2'"
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
