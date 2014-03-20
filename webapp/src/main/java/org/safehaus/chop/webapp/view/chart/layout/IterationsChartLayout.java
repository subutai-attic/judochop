package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.builder.ChartBuilder;

public class IterationsChartLayout extends ChartLayout {

    public IterationsChartLayout(ChartLayoutContext viewContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super(viewContext, chartBuilder, prevLayout, nextLayout, "iterationsChart", "iterationsChartCallback");
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        System.out.println(json);
    }

}
