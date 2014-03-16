package org.safehaus.chop.webapp.service.calc;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.*;

public class OverviewPercentile {

    static Map<String, Map<Integer, Metric>> filter(Map<String, Map<Integer, Metric>> values, int percent) {

        double percentile = new DescriptiveStatistics( toArray(values) ).getPercentile(percent);

        return filterValues(values, percentile);
    }

    private static Map<String, Map<Integer, Metric>> filterValues(Map<String, Map<Integer, Metric>> values, double percentile) {

        Map<String, Map<Integer, Metric>> filteredValues = new LinkedHashMap<String, Map<Integer, Metric>>();

        for (String commitId : values.keySet()) {
            Map<Integer, Metric> runs = values.get(commitId);

            for (Integer runNumber : runs.keySet()) {
                Metric metric = runs.get(runNumber);

                if (metric.getValue() > percentile) {
                    continue;
                }

                getRuns(filteredValues, commitId).put(runNumber, metric);
            }
        }

        return filteredValues;
    }

    private static Map<Integer, Metric> getRuns(Map<String, Map<Integer, Metric>> filteredValues, String commitId) {

        Map<Integer, Metric> filteredRuns = filteredValues.get( commitId );

        if (filteredRuns == null) {
            filteredRuns = new HashMap<Integer, Metric>();
            filteredValues.put(commitId, filteredRuns);
        }

        return filteredRuns;
    }

    private static double[] toArray(Map<String, Map<Integer, Metric>> values) {

        int size = 0;
        for (Map<Integer, Metric> runs : values.values()) {
            size += runs.size();
        }

        double arr[] = new double[size];
        int i = 0;
        for (Map<Integer, Metric> runs : values.values()) {
            for (Metric metric : runs.values()) {
                arr[i] = metric.getValue();
                i++;
            }
        }

        return arr;
    }

}
