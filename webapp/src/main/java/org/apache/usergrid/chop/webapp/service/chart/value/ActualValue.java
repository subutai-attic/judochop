package org.apache.usergrid.chop.webapp.service.chart.value;

import org.apache.usergrid.chop.api.Run;

class ActualValue extends Value {

    @Override
    protected void calcValue(Run run) {
        value += run.getActualTime();
    }

}
