package org.safehaus.chop.webapp.view.chart.format;

import org.json.simple.JSONArray;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.Collection;

public class VerticalLineFormat extends LineFormat {

    private int x;

    public VerticalLineFormat(int x) {
        this.x = x;
    }

    protected JSONArray getPoints(Collection<Metric> metrics) {

        JSONArray arr = new JSONArray();

        for (Metric m : metrics) {
            arr.add( getPoint(x, m) );
        }

        return arr;
    }

}
