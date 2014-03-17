package org.safehaus.chop.webapp.service.metric;

import org.safehaus.chop.api.Run;

public class MaxMetric extends Metric {

    public void calc(Run run) {
        value = Math.max(value, run.getMaxTime());
    }
}
