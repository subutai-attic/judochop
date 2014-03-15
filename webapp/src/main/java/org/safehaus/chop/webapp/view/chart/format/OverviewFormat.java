package org.safehaus.chop.webapp.view.chart.format;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.webapp.view.util.FileUtil;

public class OverviewFormat {

    public static String get() {

        String s = FileUtil.getContent("js/overview-chart.js");
//        s = s.replace("$categories", "'7072b19e', 'cc47b827'");
        s = s.replace("$categories", "'7072b19e'");

        String series = FileUtil.getContent("js/series.js");
//        s = s.replace("$series", series);
        s = s.replace("$series", getData());

        return s;
    }

    public static String getData() {

        JSONObject json = new JSONObject();

        json.put("name", "7072b19e");
        json.put("dashStyle", "shortdot");
        json.put("lineColor", "blue");

/*        JSONObject data = new JSONObject();
        data.put("x", 0);
        data.put("y", 10);

        JSONObject marker = new JSONObject();
        marker.put("radius", 4);
        marker.put("fillColor", "red");

        JSONObject info = new JSONObject();
        info.put("chopType", "IterationChop");

        data.put("marker", marker);
        data.put("info", info);*/

        JSONArray arr = new JSONArray();
        arr.add(getData(10));
        arr.add(getData(20));

        json.put("data", arr);

        return json.toString();
    }

    private static JSONObject getData(int y) {

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


}

