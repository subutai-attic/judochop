package org.safehaus.chop.webapp.service.calc.iterations;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationsPercentile {

    static Map<Run, List<RunResult>> filter(Map<Run, List<RunResult>> runResults, int percent) {

        double percentile = new DescriptiveStatistics( toArray(runResults) ).getPercentile(percent);

        return filterValues(runResults, percentile);
    }

    private static Map<Run, List<RunResult>> filterValues(Map<Run, List<RunResult>> runResults, double percentile) {

        HashMap<Run, List<RunResult>> resultMap = new HashMap<Run, List<RunResult>>();

        for ( Run run : runResults.keySet() ) {

            ArrayList<RunResult> resultList = new ArrayList<RunResult>();
            resultMap.put( run, resultList );

            for ( RunResult runResult : runResults.get( run ) ) {
                RunResult result = runResult.getRunTime() <= percentile ? runResult : null;
                resultList.add( result );
            }
        }

        return resultMap;
    }

    private static double[] toArray(Map<Run, List<RunResult>> runResults) {

        int size = 0;
        for ( List<RunResult> list : runResults.values() ) {
            size += list.size();
        }

        double arr[] = new double[size];
        int i = 0;
        for ( List<RunResult> list : runResults.values() ) {
            for ( RunResult runResult : list ) {
                arr[i] = runResult.getRunTime();
                i++;
            }
        }

        return arr;
    }

}
