package org.safehaus.chop.webapp.view.chart.runs;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.safehaus.chop.webapp.service.calc.overview.OverviewAvg;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.service.metric.Metric;
import org.safehaus.chop.webapp.view.chart.overview.LineFormat;
import org.safehaus.chop.webapp.view.chart.overview.VerticalLineFormat;

import java.util.Map;
import java.util.Set;

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

        arr.add( new LineFormat().getLine( collector.getRuns().values() ) );

        series = arr.toString();
        return series;
    }

}

