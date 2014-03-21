package org.safehaus.chop.webapp.view.chart.format;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.Series;

import java.util.List;

public class SeriesFormat {

    public static String format(List<Series> seriesList) {

        JSONArray arr = new JSONArray();

        for (Series series : seriesList) {
            arr.add( format(series) );
        }

        return arr.toString();
    }

    private static JSONObject format(Series series) {
        JSONObject json = new JSONObject();

        json.put( "dashStyle", "shortdot" );
        json.put( "lineColor", "blue" );
        json.put( "data", getPoints( series.getPoints() ) );

        return json;
    }

    private static JSONArray getPoints(List<Point> points) {

        JSONArray arr = new JSONArray();

        for (Point point : points) {
            arr.add( getPoint(point) );
        }

        return arr;
    }

    protected static JSONObject getPoint(Point point) {

        JSONObject data = new JSONObject();
        data.put( "x", point.getX() );
        data.put( "y", point.getY() );

        JSONObject marker = new JSONObject();
        marker.put( "radius", getRadius(point) );
        marker.put( "fillColor", getColor(point) );

        data.put( "marker", marker );

        return data;
    }

    private static int getRadius(Point point) {
        int radius = 4;

        if (point.getFailures() > 1000) {
            radius = 15;
        } else if (point.getFailures() > 500) {
            radius = 10;
        } else if (point.getFailures() > 100) {
            radius = 7;
        }

        return radius;
    }

    private static String getColor(Point point) {
        String color = "white";

//        if (point.getIgnores() > 0) {
//            color = "yellow";
//        }

        if (point.getFailures() > 0) {
            color = "red";
        }

        return color;
    }

}
