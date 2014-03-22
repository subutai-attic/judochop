package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;

public class IterationsChartLayout extends ChartLayout {

    public IterationsChartLayout(ChartLayoutContext viewContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super(viewContext, chartBuilder, prevLayout, nextLayout, "iterationsChart", "iterationsChartCallback", "js/iterations-chart.js");
    }

    @Override
    public void call(JSONArray args) throws JSONException {
//        JSONObject json = args.getJSONObject(0);
//        System.out.println(json);
    }

}
