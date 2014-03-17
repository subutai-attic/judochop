package org.safehaus.chop.webapp.service.calc.runs;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.metric.MinMetric;

import java.util.Map;
import java.util.TreeMap;

public class RunsPercentile {

    static Map<Integer, MinMetric> filter(Map<Integer, MinMetric> runs, int percent) {

        double percentile = new DescriptiveStatistics( toArray(runs) ).getPercentile(percent);

        return filterValues(runs, percentile);
    }

    private static Map<Integer, MinMetric> filterValues(Map<Integer, MinMetric> runs, double percentile) {

        Map<Integer, MinMetric> filteredRuns = new TreeMap<Integer, MinMetric>();

        for (Map.Entry<Integer, MinMetric> e : runs.entrySet()) {
            if (e.getValue().getValue() <= percentile) {
                filteredRuns.put(e.getKey(), e.getValue());
            }
        }

        return filteredRuns;
    }

    private static double[] toArray(Map<Integer, MinMetric> runs) {

        double arr[] = new double[runs.size()];
        int i = 0;
        for (MinMetric metric : runs.values()) {
            arr[i] = metric.getValue();
            i++;
        }

        return arr;
    }
}
