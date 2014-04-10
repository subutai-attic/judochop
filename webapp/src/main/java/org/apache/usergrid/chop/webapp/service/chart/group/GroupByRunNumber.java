package org.apache.usergrid.chop.webapp.service.chart.group;

<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/service/chart/group/GroupByRunNumber.java
import org.apache.usergrid.chop.api.Run;
import org.safehaus.chop.webapp.service.chart.value.*;
import org.safehaus.chop.webapp.service.chart.Params.Metric;
=======
import org.apache.usergrid.chop.webapp.service.chart.value.Value;
import org.apache.usergrid.chop.webapp.service.chart.value.ValueFactory;
import org.safehaus.chop.api.Run;
import org.apache.usergrid.chop.webapp.service.chart.Params.Metric;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/service/chart/group/GroupByRunNumber.java

import java.util.Collection;
import java.util.HashMap;

public class GroupByRunNumber {

    // <runNumber, Value>
    private HashMap<Integer, Value> runNumberValues = new HashMap<Integer, Value>();
    private Metric metric;

    public GroupByRunNumber(Collection<Run> runs, Metric metric) {
        this.metric = metric;
        group(runs);
    }

    private void group(Collection<Run> runs) {
        for (Run run : runs) {
            put(run);
        }
    }

    private void put(Run run) {

        Value value = runNumberValues.get( run.getRunNumber() );

        if (value == null) {
            value = ValueFactory.get(metric);
            runNumberValues.put(run.getRunNumber(), value);
        }

        value.merge(run);
    }

    public Collection<Value> get() {
        return runNumberValues.values();
    }

}
