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
        int i = 0;

        for (RunResult runResult : runResults) {
            arr.add( getPoint(i, runResult) );
            i++;
        }

        return arr;
    }

    protected static JSONObject getPoint(int x, RunResult runResult) {

        JSONObject data = new JSONObject();
        data.put("x", x);
        data.put("y", runResult.getRunTime());

        JSONObject marker = new JSONObject();
        marker.put("radius", 4);
        marker.put("fillColor", "red");

        JSONObject info = new JSONObject();
        info.put("chopType", "IterationChop");
        info.put("failures", runResult.getFailureCount());

        data.put("marker", marker);
        data.put("info", info);

        return data;
    }
}
