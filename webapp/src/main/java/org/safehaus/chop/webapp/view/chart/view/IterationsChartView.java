package org.safehaus.chop.webapp.view.chart.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartViewContext;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsChart;
import org.safehaus.chop.webapp.view.chart.runs.RunsChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

public class IterationsChartView extends ChartView {

    private IterationsChart iterationsChart = new IterationsChart();

    public IterationsChartView(ChartViewContext viewContext, ChartView prevView, ChartView nextView) {
        super(viewContext, prevView, nextView);
        addChartLayout("iterationsChart");
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        System.out.println(json);
    }

    public void show(Params params) {
        populateTestNames( params.getModuleId() );

        String chart = iterationsChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("iterationsChartCallback", this);
    }
}
