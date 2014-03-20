package org.safehaus.chop.webapp.view.chart.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;

public class OverviewChartView extends ChartView {

//    private OverviewChart overviewChart = new OverviewChart();

    public OverviewChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView, "overviewChart");
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
