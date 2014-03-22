package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;

public class IterationsChartLayout extends ChartLayout {

    public IterationsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super( new Config(layoutContext, chartBuilder, prevLayout, nextLayout, "iterationsChart", "iterationsChartCallback", "js/iterations-chart.js") );
    }

    private void setControlsReadOnly(boolean readOnly) {
        testNamesCombo.setReadOnly(readOnly);
        metricCombo.setReadOnly(readOnly);
    }


    public void show(Params params) {
        setControlsReadOnly(false);
        super.show(params);
        setControlsReadOnly(true);
    }

    @Override
    public void call(JSONArray args) throws JSONException {
//        JSONObject json = args.getJSONObject(0);
//        System.out.println(json);
    }

}
