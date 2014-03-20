package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.builder.ChartBuilder;

public class OverviewChartLayout extends ChartLayout {

    public OverviewChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout prevLayout, ChartLayout nextLayout) {
        super(layoutContext, chartBuilder, prevLayout, nextLayout, "overviewChart", "overviewChartCallback");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        params.setCommitId( json.getString("commitId") );
    }
}
