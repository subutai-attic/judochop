package org.safehaus.chop.webapp.view.chart.format;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Collection;

public class LineFormat {

    public JSONObject getLine(Collection<Metric> metrics) {

        JSONObject json = new JSONObject();

        json.put( "dashStyle", "shortdot" );
        json.put( "lineColor", "blue" );
        json.put( "data", getPoints(metrics) );

        return json;
    }

    protected JSONArray getPoints(Collection<Metric> metrics) {

        JSONArray arr = new JSONArray();
        int i = 0;

        for (Metric m : metrics) {
            arr.add( getPoint(i, m) );
            i++;
        }

        return arr;
    }

    protected static JSONObject getPoint(int x, Metric metric) {

        JSONObject data = new JSONObject();
        data.put("x", x);
        data.put("y", metric.getValue());

        JSONObject marker = new JSONObject();
        marker.put("radius", 4);
        marker.put("fillColor", "red");

        JSONObject info = new JSONObject();
        info.put("chopType", "IterationChop");
        info.put("failures", metric.getFailures());

        data.put("marker", marker);
        data.put("info", info);

        return data;
    }
}
