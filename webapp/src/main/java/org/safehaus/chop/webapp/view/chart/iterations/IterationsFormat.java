package org.safehaus.chop.webapp.view.chart.iterations;

import org.json.simple.JSONArray;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.calc.iterations.IterationsCollector;

import java.util.List;
import java.util.Map;

public class IterationsFormat {

    private IterationsCollector collector;
    private String series;

    public IterationsFormat(IterationsCollector collector) {
        this.collector = collector;
    }

    public String getSeries() {

        if (series != null) {
            return series;
        }

        JSONArray arr = new JSONArray();

        Map<Run, List<RunResult>> runResults = collector.getRunResults();

        for (Run run : runResults.keySet()) {
            arr.add( new LineFormat().getLine( run.getRunner(), runResults.get(run) ) );
        }

        arr.add( new LineFormat().getLine( "AVG", IterationsAvg.get(runResults) ) );

        series = arr.toString();
        return series;
    }

}

