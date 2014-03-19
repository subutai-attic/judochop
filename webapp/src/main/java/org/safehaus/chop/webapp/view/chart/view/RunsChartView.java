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
    private int runNumber;

    private String moduleId;
    private String commitId;

    public RunsChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView);
        addChartLayout("runsChart");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        runNumber = json.getInt("runNumber");
    }

    @Override
    protected void nextChartButtonClicked() {
        chartViewContext.show( nextChartView, getParams() );
    }

    private Params getParams() {
        return new Params(
                moduleId,
                (String) testNamesCombo.getValue(),
                commitId,
                runNumber,
                (String) metricCombo.getValue(),
                Integer.parseInt( (String) percentileCombo.getValue() ),
                (String) failureCombo.getValue()
        );
    }

    public void show(Params params) {

        this.moduleId = params.getModuleId();
        this.commitId = params.getCommitId();

        populateTestNames( params.getModuleId() );

        String chart = runsChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("runsChartCallback", this);
    }
}
