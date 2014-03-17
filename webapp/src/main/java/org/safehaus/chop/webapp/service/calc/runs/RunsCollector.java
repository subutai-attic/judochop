package org.safehaus.chop.webapp.service.calc.runs;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.metric.MinMetric;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RunsCollector {

    // <runNumber, metric>
    private final Map<Integer, MinMetric> runs = new TreeMap<Integer, MinMetric>();

    public RunsCollector(List<Run> list) {
        for (Run run : list) {
            collect(run);
        }
    }

    private void collect(Run run) {

        MinMetric metric = runs.get( run.getRunNumber() );

        if (metric == null) {
            metric = new MinMetric();
            runs.put(run.getRunNumber(), metric);
        }

        metric.merge(run);
    }

    public Map<Integer, MinMetric> getRuns() {
        Map<Integer, MinMetric> filteredRuns = RunsPercentile.filter(runs, 100);
        return FailureFilter.filter(filteredRuns, null);
    }

    @Override
    public String toString() {
        return runs.toString();
    }
}
