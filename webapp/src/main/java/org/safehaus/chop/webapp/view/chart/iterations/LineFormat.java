package org.safehaus.chop.webapp.view.chart.iterations;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Collection;

public class LineFormat {

    public JSONObject getLine(String name, Collection<RunResult> runResults) {

        JSONObject json = new JSONObject();

        json.put( "name", name );
        json.put( "dashStyle", "shortdot" );
        json.put( "lineColor", "blue" );
        json.put( "data", getPoints(runResults) );

        return json;
    }

    protected JSONArray getPoints(Collection<RunResult> runResults) {
        JSONArray arr = new JSONArray();
        int i = 1;

        for ( RunResult runResult : runResults ) {
            arr.add( getPoint( i, runResult ) );
            i++;

            // Bug: highcharts can't display if a line contains more than 500 points
            if ( i >= 500 ) {
                break;
            }
        }

        return arr;
    }

    protected static JSONObject getPoint( int x, RunResult runResult ) {
        Integer y = null;

        if ( runResult != null && runResult.getRunTime() > -1 ) {
            y = runResult.getRunTime();
        }

        JSONObject data = new JSONObject();
        data.put( "x", x );
        data.put( "y", y );

        JSONObject marker = new JSONObject();
        JSONObject info = new JSONObject();

        if ( runResult != null ) {
            info.put( "value", y );
            info.put( "failureCount", runResult.getFailureCount() );
            info.put( "ignoreCount", runResult.getIgnoreCount() );

            marker.put( "radius", getMarkerRadius( runResult ) );
            marker.put( "fillColor", getPointColor( runResult ) );
        }

        data.put( "marker", marker );
        data.put( "info", info );

        return data;
    }

    private static int getMarkerRadius( RunResult runResult ) {
        int radius = 4;

        if ( runResult.getFailureCount() > 20 ) {
            radius = 15;
        } else if ( runResult.getFailureCount() > 10 ) {
            radius = 10;
        } else if ( runResult.getFailureCount() > 5 ) {
            radius = 7;
        }

        return radius;
    }

    private static String getPointColor( RunResult runResult ) {
        String color = "white";

        if ( runResult.getIgnoreCount() > 0 ) {
            color = "yellow";
        }

        if ( runResult.getFailureCount() > 0 ) {
            color = "red";
        }

        return color;
    }
}
