package org.safehaus.chop.webapp.service.chart.filter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class PercentileFilter {

    public static <V extends Value> Map<String, Collection<V>> filter(Map<String, Collection<V>> map, int percent) {

        double percentile = new DescriptiveStatistics( toArray(map) ).getPercentile(percent);
        Map<String, Collection<V>> resultMap = new LinkedHashMap<String, Collection<V>>();

        for ( String key : map.keySet() ) {
            resultMap.put( key, doFilter(map.get(key), percentile) );
        }

        return resultMap;
    }

    private static <V extends Value> Collection<V> doFilter(Collection<V> values, double percentile) {

        ArrayList<V> resultValues = new ArrayList<V>();

        for (V v : values) {
            if (v.getValue() <= percentile) {
                resultValues.add(v);
            }
        }

        return resultValues;
    }

    private static <V extends Value> double[] toArray(Map<String, Collection<V>> map) {

        double arr[] = {};

        for ( Collection<V> valueList : map.values() ) {
            arr = ArrayUtils.addAll( arr, toArray(valueList) );
        }

        return arr;
    }

    private static <V extends Value> double[] toArray(Collection<V> values) {

        double arr[] = new double[ values.size() ];
        int i = 0;

        for (V v : values) {
            arr[i] = v.getValue();
            i++;
        }

        return arr;
    }

}
