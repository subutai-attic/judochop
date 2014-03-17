package org.safehaus.chop.webapp.service.metric;

import org.safehaus.chop.api.Run;

public class ActualMetric extends Metric {

    public void calc(Run run) {
        value += run.getActualTime();
    }
}
