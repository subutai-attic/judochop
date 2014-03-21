package org.safehaus.chop.webapp.service.chart.value;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.service.util.JsonUtil;

public class AvgValue extends Value {

    private int count;

    private void doCalc(double d) {
        value += d;
        count++;
    }

    @Override
    protected void calcValue(Run run) {
        doCalc( run.getAvgTime() );
    }

    @Override
    public void merge(Value other) {
        if (other == null) {
            return;
        }

        doCalc(other.getValue() );
        inc(other.getFailures(), other.getIgnores() );
        copyProperties(other);
    }

    private void copyProperties(Value other) {
        JsonUtil.copy(other.properties, properties, "chopType");
        JsonUtil.copy(other.properties, properties, "commitId");
    }

    @Override
    public double getValue() {
        return value / count;
    }
}
