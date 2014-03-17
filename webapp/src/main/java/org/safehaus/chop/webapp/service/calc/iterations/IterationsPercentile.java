package org.safehaus.chop.webapp.service.calc.iterations;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IterationsPercentile {

    static Map<Run, List<RunResult>> filter(Map<Run, List<RunResult>> runResults, int percent) {

        double percentile = new DescriptiveStatistics( toArray(runResults) ).getPercentile(percent);

        return filterValues(runResults, percentile);
    }

    private static Map<Run, List<RunResult>> filterValues(Map<Run, List<RunResult>> runResults, double percentile) {

        for (List<RunResult> list : runResults.values()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getRunTime() >= percentile) {
                    list.set(i, null);
                }
            }
        }

        return runResults;
    }

    private static double[] toArray(Map<Run, List<RunResult>> runResults) {

        int size = 0;
        for (List<RunResult> list : runResults.values()) {
            size += list.size();
        }

        double arr[] = new double[size];
        int i = 0;
        for (List<RunResult> list : runResults.values()) {
            for (RunResult runResult : list) {
                arr[i] = runResult.getRunTime();
                i++;
            }
        }

        return arr;
    }

}
