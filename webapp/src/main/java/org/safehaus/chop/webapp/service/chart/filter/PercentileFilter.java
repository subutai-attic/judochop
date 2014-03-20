package org.safehaus.chop.webapp.service.chart.filter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.chart.value.RunValue;

import java.util.*;

public class PercentileFilter {

    public static Map<String, Collection<RunValue>> filter(Map<String, Collection<RunValue>> map, int percent) {

        double percentile = new DescriptiveStatistics( toArray(map) ).getPercentile(percent);
        Map<String, Collection<RunValue>> resultMap = new LinkedHashMap<String, Collection<RunValue>>();

        for ( String key : map.keySet() ) {
            resultMap.put( key, doFilter(map.get(key), percentile) );
        }

        return resultMap;
    }

    private static Collection<RunValue> doFilter(Collection<RunValue> values, double percentile) {

        ArrayList<RunValue> resultValues = new ArrayList<RunValue>();

        for (RunValue value : values) {
            if (value.getValue() <= percentile) {
                resultValues.add(value);
            }
        }

        return resultValues;
    }

    private static double[] toArray(Map<String, Collection<RunValue>> map) {

        double arr[] = {};

        for ( Collection<RunValue> valueList : map.values() ) {
            arr = ArrayUtils.addAll( arr, toArray(valueList) );
        }

        return arr;
    }

    private static double[] toArray(Collection<RunValue> values) {

        double arr[] = new double[ values.size() ];
        int i = 0;

        for (RunValue value : values) {
            arr[i] = value.getValue();
            i++;
        }

        return arr;
    }

}
