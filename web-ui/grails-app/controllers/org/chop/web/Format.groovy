package org.chop.web

import org.chop.service.value.Value

class Format {

    static String formatCommit(int x, String commitId, List<Value> values) {

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

    private static String formatPoint(int x, Value value) {

        String pointColor = value.failures > 0 ? 'red' : 'white'

        if (value.failures == 0) {
            pointColor = value.ignores > 0 ? 'yellow' : 'white'
        }

        """{
                x: $x,
                y: ${value.value},
                marker: { fillColor: '$pointColor' },
                info: ${getPointInfo(value)}
        }"""
    }

    private static String getPointInfo(Value value) {
        """'failures: ${value.failures}, ignores: ${value.ignores}'"""
    }

}
