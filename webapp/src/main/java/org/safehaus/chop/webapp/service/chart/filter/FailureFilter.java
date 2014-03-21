package org.safehaus.chop.webapp.service.chart.filter;

import org.safehaus.chop.webapp.service.chart.value.RunValue;

import java.util.*;

public class FailureFilter {

    public static Map<String, Collection<RunValue>> filter(Map<String, Collection<RunValue>> map, String type) {

        Map<String, Collection<RunValue>> resultMap = new LinkedHashMap<String, Collection<RunValue>>();

        for ( String key : map.keySet() ) {
            resultMap.put( key, doFilter(map.get(key), type) );
        }

        return resultMap;
    }

    private static Collection<RunValue> doFilter(Collection<RunValue> values, String type) {
        ArrayList<RunValue> resultValues = new ArrayList<RunValue>();

        for (RunValue value : values) {
            if ("ALL".equals(type)
                    || ("FAILED".equals(type) && value.getFailures() > 0)
                    || ("SUCCESS".equals(type) && value.getFailures() == 0)
                    ) {
                resultValues.add(value);
            }
        }

        return resultValues;
    }
}
