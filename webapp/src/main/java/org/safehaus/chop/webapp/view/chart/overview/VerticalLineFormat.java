package org.safehaus.chop.webapp.view.chart.overview;

import org.json.simple.JSONArray;
import org.safehaus.chop.webapp.service.metric.Metric;
import org.safehaus.chop.webapp.service.metric.MinMetric;

import java.util.Collection;

public class VerticalLineFormat extends LineFormat {

    protected JSONArray getPoints(Collection<Metric> metrics, int x) {

        JSONArray arr = new JSONArray();

        for (Metric m : metrics) {
            arr.add( getPoint(x, m) );
        }

        return arr;
    }

}
