/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.webapp.view.chart.format;

import org.apache.usergrid.chop.webapp.service.chart.Point;
import org.apache.usergrid.chop.webapp.service.chart.series.Series;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class SeriesFormat {

    @SuppressWarnings("unchecked")
    public static String format(List<Series> series, PointRadius pointRadius) {

        JSONArray arr = new JSONArray();

        for (Series s : series) {
            arr.add( format(s, pointRadius) );
        }

        return arr.toString();
    }

    @SuppressWarnings("unchecked")
    private static JSONObject format(Series series, PointRadius pointRadius) {

        JSONObject json = new JSONObject();

        json.put("name", series.getName() );
        json.put("dashStyle", "shortdot");
        json.put("data", getPoints(series.getPoints(), pointRadius) );

        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getPoints(List<Point> points, PointRadius pointRadius) {

        JSONArray arr = new JSONArray();

        // Bug: Highcharts can't display more than 500 points
        final int MAX_POINTS = 500;
        int len = Math.min(points.size(), MAX_POINTS);

        for (int i = 0; i < len; i++) {
            arr.add( getPoint(points.get(i), pointRadius) );
        }

        return arr;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject getPoint(Point point, PointRadius pointRadius) {

        JSONObject data = new JSONObject();
        data.put("x", point.getX() );
        data.put("y", point.getY() );
        data.put("properties", point.getProperties() );

        JSONObject marker = new JSONObject();
        marker.put("radius", pointRadius.get(point) );
        marker.put("fillColor", getColor(point) );

        data.put("marker", marker );

        return data;
    }

    private static String getColor(Point point) {

        String color = "white";

        if (point.getFailures() > 0) {
            color = "red";
        } else if (point.getIgnores() > 0) {
            color = "yellow";
        }

        return color;
    }
}
