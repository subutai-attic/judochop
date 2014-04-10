package org.apache.usergrid.chop.webapp.service.chart.filter;

import org.apache.usergrid.chop.webapp.service.chart.Params;
import org.apache.usergrid.chop.webapp.service.chart.value.Value;

import java.util.*;

public class FailureFilter {

    public static Map<String, Collection<Value>> filter(Map<String, Collection<Value>> map, Params.FailureType failureType) {

        Map<String, Collection<Value>> resultMap = new LinkedHashMap<String, Collection<Value>>();

        for (String key : map.keySet() ) {
            resultMap.put(key, filter(map.get(key), failureType) );
        }

        return resultMap;
    }

    public static Collection<Value> filter(Collection<Value> values, Params.FailureType failureType) {

        ArrayList<Value> resultValues = new ArrayList<Value>();

        for (Value value : values) {
            Value newValue = isValid(value, failureType) ? value : null;
            resultValues.add(newValue);
        }

        return resultValues;
    }

    private static boolean isValid(Value value, Params.FailureType failureType) {
        return failureType == Params.FailureType.ALL
                || failureType == Params.FailureType.FAILED && value.getFailures() > 0
                || failureType == Params.FailureType.SUCCESS && value.getFailures() == 0;
    }
}
