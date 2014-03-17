package org.safehaus.chop.webapp.service.calc.iterations;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;

import java.util.List;
import java.util.Map;

public class IterationsCollector {

    private Map<Run, List<RunResult>> runResults;

    public IterationsCollector(Map<Run, List<RunResult>> runResults) {
        this.runResults = runResults;
    }

    public Map<Run, List<RunResult>> getRunResults() {
        Map<Run, List<RunResult>> filteredValues = IterationsPercentile.filter(runResults, 100);
        return FailureFilter.filter(filteredValues, null);
    }

    @Override
    public String toString() {
        return runResults.toString();
    }
}
