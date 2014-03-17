package org.safehaus.chop.webapp.service.calc.runs;

import org.safehaus.chop.webapp.service.metric.MinMetric;

import java.util.Map;
import java.util.TreeMap;

public class FailureFilter {

    static Map<Integer, MinMetric> filter(Map<Integer, MinMetric> runs, String type) {

        Map<Integer, MinMetric> filteredRuns = new TreeMap<Integer, MinMetric>();

        for (Map.Entry<Integer, MinMetric> e : runs.entrySet()) {
            MinMetric metric = e.getValue();
            if (type == null
                    || ("FAILED".equals(type) && metric.getFailures() > 0)
                    || ("SUCCESS".equals(type) && metric.getFailures() == 0)
                    ) {
                filteredRuns.put(e.getKey(), metric);
            }
        }

        return filteredRuns;
    }
}
