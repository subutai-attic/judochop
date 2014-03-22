package org.safehaus.chop.webapp.service.chart.builder.average;

import org.safehaus.chop.webapp.service.chart.value.AvgValue;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class IterationsAverage {

    public static Collection<Value> calc(Map<String, Collection<Value>> runnerValues) {

        ArrayList<Value> avgValues = new ArrayList<Value>();

        for ( String runner : runnerValues.keySet() ) {
            int i = 0;

            for ( Value value : runnerValues.get(runner) ) {
                get(avgValues, i).merge(value);
                i++;
            }
        }

        return avgValues;
    }

    private static Value get(ArrayList<Value> values, int i) {

        if (values.size() < i + 1) {
            values.add( new AvgValue() );
        }

        return values.get(i);
    }
}
