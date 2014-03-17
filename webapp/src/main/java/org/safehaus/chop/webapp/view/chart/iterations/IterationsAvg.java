package org.safehaus.chop.webapp.view.chart.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IterationsAvg {

    public static List<RunResult> get(Map<Run, List<RunResult>> runResults) {

        ArrayList<RunResult> resultList = new ArrayList<RunResult>();

        if (runResults.isEmpty()) {
            return resultList;
        }

        int len = runResults.values().iterator().next().size();
        Set<Run> runs = runResults.keySet();

        for (int i = 0; i < len; i++) {

            AvgResult avg = new AvgResult();

            for (Run run : runs) {
                List<RunResult> values = runResults.get(run);

                RunResult runResult = values.get(i);
                avg.merge(runResult);
                System.out.println(runResult.getRunTime());
            }

            resultList.add(avg);
        }

        return resultList;
    }

}
