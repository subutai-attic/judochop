package org.safehaus.chop.webapp.view.chart.overview;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Collection;

public class LineFormat {

    public JSONObject getLine(Collection<Metric> metrics, int x) {

        JSONObject json = new JSONObject();

        json.put( "dashStyle", "shortdot" );
        json.put( "lineColor", "blue" );
        json.put( "data", getPoints(metrics, x) );

        return json;
    }

    protected JSONArray getPoints(Collection<Metric> metrics, int xstart) {

        JSONArray arr = new JSONArray();
        int i = xstart;

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
        marker.put( "radius", getMarkerRadius( metric ) );
        marker.put( "fillColor", getPointColor( metric ) );

        JSONObject info = new JSONObject();
        info.put( "value", metric.getValue() );
        info.put( "runners", metric.getRunnersString() );
        info.put( "chopType", metric.getChopType() );
        info.put( "failures", metric.getFailures() );
        info.put( "chopType", metric.getChopType() );
        info.put( "runNumber", metric.getRunNumberString() );
        info.put( "commitId", metric.getCommitId() );
        info.put( "totalTestsRun", metric.getTotalTestsRun() );
        info.put( "iterations", metric.getIterationsString() );
        info.put( "ignores", metric.getIgnores() );

        data.put( "marker", marker );
        data.put( "info", info );

        return data;
    }

    private static int getMarkerRadius( Metric metric ) {
        int radius = 4;

        if ( metric.getFailures() > 1000 ) {
            radius = 15;
        } else if ( metric.getFailures() > 500 ) {
            radius = 10;
        } else if ( metric.getFailures() > 100 ) {
            radius = 7;
        }

        return radius;
    }

    private static String getPointColor( Metric metric ) {
        String color = "white";

        if (metric.getIgnores() > 0) {
            color = "yellow";
        }

        if (metric.getFailures() > 0) {
            color = "red";
        }

        return color;
    }
}
