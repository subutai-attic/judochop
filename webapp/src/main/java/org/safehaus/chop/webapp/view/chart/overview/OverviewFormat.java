package org.safehaus.chop.webapp.view.chart.overview;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.safehaus.chop.webapp.service.calc.overview.OverviewAvg;
import org.safehaus.chop.webapp.service.calc.overview.OverviewCollector;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Map;
import java.util.Set;

public class OverviewFormat {

    private OverviewCollector collector;
    private String categories;
    private String series;

    public OverviewFormat(OverviewCollector collector) {
        this.collector = collector;
    }

    public String getCategories() {

        if (categories != null) {
            return categories;
        }

        categories = "";
        Set<String> commits = collector.getValues().keySet();

        for (String commitId : commits) {
            if (!categories.isEmpty()) {
                categories += ", ";
            }

            categories += String.format("'%s'", StringUtils.abbreviate(commitId, 10));
        }

        return categories;
    }

    public String getSeries() {

        if (series != null) {
            return series;
        }

        Map<String, Map<Integer, Metric>> values = collector.getValues();
        JSONArray arr = new JSONArray();

        addSeries(arr, values);
        arr.add( new LineFormat().getLine( OverviewAvg.get(values) ) );

        series = arr.toString();
        return series;
    }

    private void addSeries(JSONArray arr, Map<String, Map<Integer, Metric>> values) {
        int x = 0;

        for (Map<Integer, Metric> runs : values.values()) {
            arr.add( new VerticalLineFormat(x).getLine(runs.values()) );
            x++;
        }
    }


}

