package org.safehaus.chop.webapp.view.chart.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

public class RunsChartView extends ChartView {

    private RunsChart runsChart = new RunsChart();

    private String moduleId;
    private String commitId;
    private int runNumber;

    public RunsChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView);
    }

    @Override
    protected void addControls() {
        super.addControls();
        addChartLayout("runsChart");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        runNumber = json.getInt("runNumber");
    }

    @Override
    protected Params getParams() {
        return super.getParams()
                .setModuleId(moduleId)
                .setCommitId(commitId)
                .setRunNumber(runNumber);
    }

    @Override
    public void show(Params params) {

        moduleId = params.getModuleId();
        commitId = params.getCommitId();

        populateTestNames( params.getModuleId() );

        String chart = runsChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("runsChartCallback", this);
    }
}
