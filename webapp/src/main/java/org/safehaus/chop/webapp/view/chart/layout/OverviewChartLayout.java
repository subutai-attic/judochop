package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;

public class OverviewChartLayout extends ChartLayout {

//    private OverviewChart overviewChart = new OverviewChart();

    public OverviewChartLayout(ChartLayoutContext layoutContext, ChartLayout prevLayout, ChartLayout nextLayout) {
        super(layoutContext, prevLayout, nextLayout, "overviewChart");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        params.setCommitId( json.getString("commitId") );
    }

//    public void show_(Params params_) {
//
//        this.moduleId = params_.getModuleId();
//
//        populateTestNames(moduleId);
//
//        Params params = getParams();
//
//        String chart = overviewChart.get(params);
//        JavaScriptUtil.execute(chart);
//        JavaScriptUtil.addCallback("overviewChartCallback", this);
//    }
}
