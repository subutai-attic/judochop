package org.safehaus.chop.webapp.service.chart.value;

import org.safehaus.chop.api.Run;

class ActualValue extends Value {

    @Override
    protected void calcValue(Run run) {
        value += run.getActualTime();
    }

}
