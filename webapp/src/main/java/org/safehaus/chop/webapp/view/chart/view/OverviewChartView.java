package org.safehaus.chop.webapp.view.chart.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.chart.overview.OverviewChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

public class OverviewChartView extends ChartView {

    private OverviewChart overviewChart = new OverviewChart();

    private String moduleId;
    private String commitId;

    public OverviewChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView);
    }

    @Override
    protected void addControls() {
        super.addControls();
        addChartLayout("overviewChart");
        addNextChartButton();
    }

    @Override
    protected Params getParams() {
        return super.getParams()
                .setModuleId(moduleId)
                .setCommitId(commitId);
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        commitId = json.getString("commitId");
    }

    @Override
    public void show(Params params_) {

        this.moduleId = params_.getModuleId();

        populateTestNames(moduleId);

        Params params = getParams();

        String chart = overviewChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("overviewChartCallback", this);
    }
}
