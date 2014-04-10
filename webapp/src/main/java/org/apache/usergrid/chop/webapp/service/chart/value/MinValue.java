package org.apache.usergrid.chop.webapp.service.chart.value;

import org.apache.usergrid.chop.api.Run;

class MinValue extends Value {

    MinValue() {
        value = Double.MAX_VALUE;
    }

    @Override
    protected void calcValue(Run run) {
        value = Math.min( value, run.getMinTime() );
    }

}
