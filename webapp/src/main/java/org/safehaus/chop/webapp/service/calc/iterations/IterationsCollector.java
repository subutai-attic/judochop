package org.safehaus.chop.webapp.service.calc.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.calc.runs.FailureFilter;
import org.safehaus.chop.webapp.service.calc.runs.RunsPercentile;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IterationsCollector {

    private Map<Run, List<RunResult>> runResults;

    public IterationsCollector(Map<Run, List<RunResult>> runResults) {
        this.runResults = runResults;
    }

    public Map<Run, List<RunResult>> getRunResults() {
        Map<Run, List<RunResult>> filteredValues = IterationsPercentile.filter(runResults, 10);
//        return FailureFilter.filter(filteredRuns, null);
        return filteredValues;
    }

    @Override
    public String toString() {
        return runResults.toString();
    }
}
