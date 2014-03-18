package org.safehaus.chop.webapp.service.calc.runs;

import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Map;
import java.util.TreeMap;

public class FailureFilter {

    static Map<Integer, Metric> filter(Map<Integer, Metric> runs, String type) {

        Map<Integer, Metric> filteredRuns = new TreeMap<Integer, Metric>();

        for (Map.Entry<Integer, Metric> e : runs.entrySet()) {
            Metric metric = e.getValue();

            if ("ALL".equals(type)
                    || ("FAILED".equals(type) && metric.getFailures() > 0)
                    || ("SUCCESS".equals(type) && metric.getFailures() == 0)
                    ) {
                filteredRuns.put(e.getKey(), metric);
            }
        }

        return filteredRuns;
    }
}
