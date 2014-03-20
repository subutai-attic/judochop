package org.safehaus.chop.webapp.service.chart.group;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.chart.value.RunValue;
import org.safehaus.chop.webapp.service.metric.Metric;
import org.safehaus.chop.webapp.service.metric.MetricFactory;

import java.util.Collection;
import java.util.HashMap;

public class GroupByRunNumber {

    // <runNumber, Value>
    private HashMap<Integer, RunValue> values = new HashMap<Integer, RunValue>();

    public GroupByRunNumber(Collection<Run> runs) {
        group(runs);
    }

    private void group(Collection<Run> runs) {
        for (Run run : runs) {
            put(run);
        }
    }

    private void put(Run run) {
        RunValue value = values.get( run.getRunNumber() );

        if (value == null) {
//            value = MetricFactory.getMetric(metricType);
            value = new RunValue();
            values.put(run.getRunNumber(), value);
        }

        value.merge(run);
    }

    public Collection<RunValue> get() {
        return values.values();
    }

}
