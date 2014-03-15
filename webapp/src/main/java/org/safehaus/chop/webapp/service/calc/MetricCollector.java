package org.safehaus.chop.webapp.service.calc;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.HashMap;
import java.util.Map;

public class MetricCollector {

    // <commitId>,  <runNumber, metric>
    private final Map<String, Map<Integer, Metric>> values = new HashMap<String, Map<Integer, Metric>>();

    public MetricCollector() {

    }

    public void collect(Run run) {

        Map<Integer, Metric> runs = values.get( run.getCommitId() );

        if (runs == null) {
            runs = new HashMap<Integer, Metric>();
            values.put(run.getCommitId(), runs);
        }

        Metric metric = runs.get(run.getRunNumber());

        if (metric == null) {
            metric = new Metric();
            runs.put(run.getRunNumber(), metric);
        }

        metric.merge(run);
//        System.out.println(metric);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
