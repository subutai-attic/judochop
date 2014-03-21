package org.safehaus.chop.webapp.service.chart.filter;

import org.safehaus.chop.webapp.service.chart.value.Value;

import java.util.*;

public class FailureFilter {

    public static Map<String, Collection<Value>> filter(Map<String, Collection<Value>> map, String failureType) {

        Map<String, Collection<Value>> resultMap = new LinkedHashMap<String, Collection<Value>>();

        for (String key : map.keySet() ) {
            resultMap.put(key, filter(map.get(key), failureType) );
        }

        return resultMap;
    }

    public static Collection<Value> filter(Collection<Value> values, String failureType) {

        ArrayList<Value> resultValues = new ArrayList<Value>();

        for (Value value : values) {
            Value newValue = isValid(value, failureType) ? value : null;
            resultValues.add(newValue);
        }

        return resultValues;
    }

    private static boolean isValid(Value value, String failureType) {
        return "ALL".equals(failureType)
                || ("FAILED".equals(failureType) && value.getFailures() > 0)
                || ("SUCCESS".equals(failureType) && value.getFailures() == 0);
    }
}
