package org.safehaus.chop.webapp.service.chart.filter;

import org.safehaus.chop.webapp.service.chart.value.RunValue;
import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class FailureFilter {

    public static <V extends Value> Map<String, Collection<V>> filter(Map<String, Collection<V>> map, String type) {

        Map<String, Collection<V>> resultMap = new LinkedHashMap<String, Collection<V>>();

        for ( String key : map.keySet() ) {
            resultMap.put( key, doFilter(map.get(key), type) );
        }

        return resultMap;
    }

    private static <V extends Value> Collection<V> doFilter(Collection<V> values, String type) {

        ArrayList<V> resultValues = new ArrayList<V>();

        for (V v : values) {
            V newValue = null;

            if ("ALL".equals(type)
                    || ("FAILED".equals(type) && v.getFailures() > 0)
                    || ("SUCCESS".equals(type) && v.getFailures() == 0)
                    ) {
//                resultValues.add(v);
                newValue = v;
            }

            resultValues.add(newValue);
        }

        return resultValues;
    }
}
