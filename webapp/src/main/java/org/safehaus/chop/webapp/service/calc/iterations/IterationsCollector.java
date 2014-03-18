package org.safehaus.chop.webapp.service.calc.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

import java.util.List;
import java.util.Map;

public class IterationsCollector {

    private Map<Run, List<RunResult>> runResults;
    private int percentile;
    private String failureValue;

    public IterationsCollector(Map<Run, List<RunResult>> runResults, int percentile, String failureValue) {
        this.runResults = runResults;
        this.percentile = percentile;
        this.failureValue = failureValue;
    }

    public Map<Run, List<RunResult>> getRunResults() {
        Map<Run, List<RunResult>> filteredValues = IterationsPercentile.filter(runResults, percentile);
        return FailureFilter.filter(filteredValues, failureValue);
    }

    @Override
    public String toString() {
        return runResults.toString();
    }
}
