package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

public class RunsChartLayout extends ChartLayout {

    private RunsChart runsChart = new RunsChart();

    public RunsChartLayout(ChartLayoutContext layoutContext, ChartLayout prevLayout, ChartLayout nextLayout) {
        super(layoutContext, prevLayout, nextLayout, "runsChart");
        addNextChartButton();
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        params.setRunNumber( json.getInt("runNumber") );
    }

    public void show(Params params) {

        this.params = params;

        populateTestNames( params.getModuleId() );

        String chart = runsChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("runsChartCallback", this);
    }
}
