package org.safehaus.chop.webapp.service.calc.runs;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class RunsPercentile {

    static Map<Integer, Metric> filter(Map<Integer, Metric> runs, int percent) {

        double percentile = new DescriptiveStatistics( toArray(runs) ).getPercentile(percent);

        return filterValues(runs, percentile);
    }

    private static Map<Integer, Metric> filterValues(Map<Integer, Metric> runs, double percentile) {

        Map<Integer, Metric> filteredRuns = new TreeMap<Integer, Metric>();

        for (Map.Entry<Integer, Metric> e : runs.entrySet()) {
            if (e.getValue().getValue() <= percentile) {
                filteredRuns.put(e.getKey(), e.getValue());
            }
        }

        return filteredRuns;
    }

    private static double[] toArray(Map<Integer, Metric> runs) {

        double arr[] = new double[runs.size()];
        int i = 0;
        for (Metric metric : runs.values()) {
            arr[i] = metric.getValue();
            i++;
        }

        return arr;
    }
}
