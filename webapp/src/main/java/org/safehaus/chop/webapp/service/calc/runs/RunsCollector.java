package org.safehaus.chop.webapp.service.calc.runs;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.metric.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RunsCollector {

    // <runNumber, metric>
    private final Map<Integer, Metric> runs = new TreeMap<Integer, Metric>();

    private String metricType;
    private int percentile;
    private String failureValue;

    public RunsCollector(List<Run> list, String metricType, int percentile, String failureValue) {

        this.metricType = metricType;
        this.percentile = percentile;
        this.failureValue = failureValue;

        for (Run run : list) {
            collect(run);
        }
    }

    private void collect(Run run) {

        Metric metric = runs.get( run.getRunNumber() );

        if (metric == null) {
            metric = MetricFactory.getMetric(metricType);
            runs.put(run.getRunNumber(), metric);
        }

        metric.merge(run);
    }

    public Map<Integer, Metric> getRuns() {
        Map<Integer, Metric> filteredRuns = RunsPercentile.filter(runs, percentile);
        return FailureFilter.filter(filteredRuns, failureValue);
    }

    @Override
    public String toString() {
        return runs.toString();
    }
}
