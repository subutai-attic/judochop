package org.safehaus.chop.webapp.service.chart.builder;

import org.safehaus.chop.webapp.service.chart.value.AvgValue;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class IterationsAvg {

    static Collection<AvgValue> get(Map<String, Collection<Value>> runnerValues) {

        ArrayList<AvgValue> avgValues = new ArrayList<AvgValue>();

        for ( String runner : runnerValues.keySet() ) {
            int i = 0;
            for ( Value value : runnerValues.get(runner) ) {
                get(avgValues, i).merge(value);
                i++;
            }
        }

        return avgValues;
    }

    private static AvgValue get(ArrayList<AvgValue> values, int i) {

        if (values.size() < i + 1) {
            values.add( new AvgValue() );
        }

        return values.get(i);
    }
}
