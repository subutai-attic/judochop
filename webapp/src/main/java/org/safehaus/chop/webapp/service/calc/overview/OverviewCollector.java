package org.safehaus.chop.webapp.service.calc.overview;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.metric.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OverviewCollector {

    // < <commitId>, <runNumber, metric> >
    private final Map<String, Map<Integer, Metric>> values = new LinkedHashMap<String, Map<Integer, Metric>>();
    private int percentile;
    private String metricType;
    private String failureValue;

    public OverviewCollector(List<Commit> commits, String metricType, int percentile, String failureValue) {

        this.percentile = percentile;
        this.metricType = metricType;
        this.failureValue = failureValue;

        for (Commit commit : commits) {
            values.put(commit.getId(), new HashMap<Integer, Metric>());
        }
    }

    public void collect(Run run) {

        Map<Integer, Metric> runs = values.get( run.getCommitId() );

        if (runs == null) {
            // No such commit. Probably a commit is not from a currently selected module.
            return;
        }

        Metric metric = runs.get(run.getRunNumber());

        if (metric == null) {
//            metric = new AvgMetric();
//            metric = new MinMetric();
//            metric = new MaxMetric();
//            metric = new ActualMetric();
            metric = MetricFactory.getMetric(metricType);
            runs.put(run.getRunNumber(), metric);
        }

        metric.merge(run);
    }

    public Map<String, Map<Integer, Metric>> getValues() {
        Map<String, Map<Integer, Metric>> filteredValues = OverviewPercentile.filter(values, percentile);
//        return FailureFilter.filter(filteredValues, "FAILED");
        return FailureFilter.filter(filteredValues, failureValue);
    }

    @Override
    public String toString() {
        return getValues().toString();
    }
}
