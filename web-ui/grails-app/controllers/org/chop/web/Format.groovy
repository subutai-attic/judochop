package org.chop.web

import org.chop.service.metric.AggregatedMetric
import org.chop.service.metric.Metric

class Format {

    static String formatCommit(int x, String commitId, List<Metric> values) {

        List data = values.collect {
            formatPoint(x, it)
        }

        return formatSeries(commitId, data)
    }

    static String formatValues(List values) {

        int i = 0
        List data = []

        values.each {
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

        if (value.ignores > 0) {
            pointColor = 'yellow'
        }

        if (value.failures > 0) {
            pointColor = 'red'
            markerRadius = 6
        }

        if (value instanceof AggregatedMetric) {
            markerRadius = 10
        }

        """{
                x: $x,
                y: ${value.value},
                marker: {
                    radius: ${markerRadius},
                    fillColor: '$pointColor'
                },
                info: ${getPointInfo(value)}
        }"""
    }

    private static String getPointInfo(Metric value) {
        """'- chopType: ${value.chopType} <br/>- runNumber: ${value.runNumber} <br/>- totalTestsRun: ${value.totalTestsRun} <br/>- iterations: ${value.iterations} <br/>- failures: ${value.failures} <br/>- ignores: ${value.ignores}'"""
    }

}
