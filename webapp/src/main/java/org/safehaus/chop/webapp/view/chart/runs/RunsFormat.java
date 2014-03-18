package org.safehaus.chop.webapp.view.chart.runs;

import org.json.simple.JSONArray;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.view.chart.overview.LineFormat;

public class RunsFormat {

    private RunsCollector collector;
    private String series;

    public RunsFormat(RunsCollector collector) {
        this.collector = collector;
    }

    public String getSeries() {

        if (series != null) {
            return series;
        }

        JSONArray arr = new JSONArray();

        arr.add( new LineFormat().getLine( collector.getRuns().values(), 1) );

        series = arr.toString();
        return series;
    }

}

