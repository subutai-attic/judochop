package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsChart;
import org.safehaus.chop.webapp.view.util.JavaScriptUtil;

public class IterationsChartLayout extends ChartLayout {

    private IterationsChart iterationsChart = new IterationsChart();

    public IterationsChartLayout(ChartLayoutContext viewContext, ChartLayout prevLayout, ChartLayout nextLayout) {
        super(viewContext, prevLayout, nextLayout, "iterationsChart");
    }

    @Override
    public void call(JSONArray args) throws JSONException {
        JSONObject json = args.getJSONObject(0);
        System.out.println(json);
    }

    @Override
    public void show(Params params) {
        populateTestNames( params.getModuleId() );
        String chart = iterationsChart.get(params);
        JavaScriptUtil.execute(chart);
        JavaScriptUtil.addCallback("iterationsChartCallback", this);
    }
}
