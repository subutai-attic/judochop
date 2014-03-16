package org.safehaus.chop.webapp.service.calc.overview;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FailureFilter {

    static Map<String, Map<Integer, Metric>> filter(Map<String, Map<Integer, Metric>> values, String type) {

        Map<String, Map<Integer, Metric>> filteredValues = new LinkedHashMap<String, Map<Integer, Metric>>();

        for (String commitId : values.keySet()) {
            Map<Integer, Metric> runs = values.get(commitId);

            for (Integer runNumber : runs.keySet()) {
                Metric metric = runs.get(runNumber);

                if (type == null
                        || ("FAILED".equals(type) && metric.getFailures() > 0)
                        || ("SUCCESS".equals(type) && metric.getFailures() == 0)
                        ) {
                    getRuns(filteredValues, commitId).put(runNumber, metric);
                }
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

}
