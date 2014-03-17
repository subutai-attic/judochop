package org.safehaus.chop.webapp.service.metric;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class MinMetric extends Metric {

    public MinMetric() {
        value = Double.MAX_VALUE;
    }

    public void calc(Run run) {
        value = Math.min(value, run.getMinTime());
    }
}
