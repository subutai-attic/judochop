package org.apache.usergrid.chop.webapp.service.chart.value;

<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/service/chart/value/AvgValue.java
import org.apache.usergrid.chop.api.Run;
import org.safehaus.chop.webapp.service.util.JsonUtil;
=======
import org.apache.usergrid.chop.webapp.service.util.JsonUtil;
import org.safehaus.chop.api.Run;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/service/chart/value/AvgValue.java

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
