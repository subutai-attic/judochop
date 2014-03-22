package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;

public class IterationsChartLayout extends ChartLayout {

    public IterationsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super( new Config(
                layoutContext,
                chartBuilder,
                prevLayout,
                nextLayout,
                "iterationsChart",
                "iterationsChartCallback",
                "js/iterations-chart.js",
                new PointRadius(20, 15, 10, 10, 5, 7)
        ) );
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
    protected void pointClicked(JSONObject json) throws JSONException {
        detailsTable.setContent(json);
    }

}
