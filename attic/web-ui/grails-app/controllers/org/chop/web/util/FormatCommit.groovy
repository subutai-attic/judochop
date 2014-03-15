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
