package org.apache.usergrid.chop.webapp.service.chart.group;

<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/service/chart/group/GroupByRunner.java
import org.apache.usergrid.chop.api.Run;
import org.apache.usergrid.chop.api.RunResult;
import org.safehaus.chop.webapp.service.chart.value.Value;
=======
import org.apache.usergrid.chop.webapp.service.chart.value.Value;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/service/chart/group/GroupByRunner.java

import java.util.*;

public class GroupByRunner {

    // <runner, List<Value>>
    public static Map<String, Collection<Value>> group(Map<Run, List<RunResult>> runResults) {

        Map<String, Collection<Value>> runnerValues = new HashMap<String, Collection<Value>>();

        for ( Run run : runResults.keySet() ) {
            runnerValues.put(run.getRunner(), toValueList(runResults.get(run) ) );
        }

        return runnerValues;
    }

    private static List<Value> toValueList(List<RunResult> runResults) {

        ArrayList<Value> values = new ArrayList<Value>();

        for (RunResult runResult : runResults) {
            values.add(new Value(runResult) );
        }

        return values;
    }
}
