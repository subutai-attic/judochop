package org.apache.usergrid.chop.webapp.service.chart.builder.average;

import org.apache.usergrid.chop.webapp.service.chart.value.Value;
import org.apache.usergrid.chop.webapp.service.chart.value.AvgValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class OverviewAverage {

    public static Collection<Value> calc(Map<String, Collection<Value>> commitRuns) {

        ArrayList<Value> avgValues = new ArrayList<Value>();

        for ( String commitId : commitRuns.keySet() ) {
            Collection<Value> values = commitRuns.get(commitId);
            avgValues.add(getAvg(values) );
        }

        return avgValues;
    }

    private static Value getAvg(Collection<Value> values) {

        Value avg = new AvgValue();

        for (Value value : values) {
            avg.merge(value);
        }

        return avg;
    }

}
