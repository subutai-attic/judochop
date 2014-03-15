package org.safehaus.chop.webapp.view.chart.format;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.webapp.service.calc.OverviewCollector;
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
        int x = 0;

        for (Map<Integer, Metric> runs : values.values()) {
            arr.add( getLine(x, runs) );
            x++;
        }

        series = arr.toString();

        return series;
    }

    public static JSONObject getLine(int x, Map<Integer, Metric> runs) {

        JSONObject json = new JSONObject();

        json.put("name", ""+x);
        json.put("dashStyle", "shortdot");
        json.put("lineColor", "blue");

        JSONArray arr = new JSONArray();

        for (Metric m : runs.values()) {
            arr.add( getPoint(x, m) );
        }


//        arr.add(getData(x, 20));

        json.put("data", arr);

        return json;
    }

    private static JSONObject getPoint(int x, Metric metric) {

        JSONObject data = new JSONObject();
        data.put("x", x);
        data.put("y", metric.getValue());

        JSONObject marker = new JSONObject();
        marker.put("radius", 4);
        marker.put("fillColor", "red");

        JSONObject info = new JSONObject();
        info.put("chopType", "IterationChop");

        data.put("marker", marker);
        data.put("info", info);

        return data;
    }


/*

    private static JSONObject getData_(int x, int y) {

        JSONObject data = new JSONObject();
        data.put("x", 0);
        data.put("y", y);

        JSONObject marker = new JSONObject();
        marker.put("radius", 4);
        marker.put("fillColor", "red");

        JSONObject info = new JSONObject();
        info.put("chopType", "IterationChop");

        data.put("marker", marker);
        data.put("info", info);

        return data;
    }

    public static String getData_(int x) {

        JSONObject json = new JSONObject();

        json.put("name", "7072b19e");
        json.put("dashStyle", "shortdot");
        json.put("lineColor", "blue");

        JSONArray arr = new JSONArray();
        arr.add(getData(x, 10));
        arr.add(getData(x, 20));

        json.put("data", arr);

        return json.toString();
    }
*/



}

