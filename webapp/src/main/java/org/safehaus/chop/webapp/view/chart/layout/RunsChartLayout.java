package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.builder.ChartBuilder;

public class RunsChartLayout extends ChartLayout {

    public RunsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super(layoutContext, chartBuilder, prevLayout, nextLayout, "runsChart", "runsChartCallback");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        params.setRunNumber( json.getInt("runNumber") );
    }
}
