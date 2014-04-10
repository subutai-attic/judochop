package org.apache.usergrid.chop.webapp.service.chart.value;

import org.apache.usergrid.chop.webapp.service.chart.Params;

public class ValueFactory {

    public static Value get(Params.Metric metric) {

        Value value;

//        if (metric == Metric.AVG"Avg Time".equals(metricType)) {
//            value = new AvgValue();
//        } else if ("Min Time".equals(metricType)) {
//            value = new MinValue();
//        } else if ("Max Time".equals(metricType)) {
//            value = new MaxValue();
//        } else {
//            value = new ActualValue();
//        }

        switch (metric) {
            case AVG:
                value = new AvgValue();
                break;
            case MIN:
                value = new MinValue();
                break;
            case MAX:
                value = new MaxValue();
                break;
            default:
                value = new ActualValue();
                break;

        }

        return value;
    }
}
