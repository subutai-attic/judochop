package org.safehaus.chop.webapp.service.chart.group;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.chart.value.Value;

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
