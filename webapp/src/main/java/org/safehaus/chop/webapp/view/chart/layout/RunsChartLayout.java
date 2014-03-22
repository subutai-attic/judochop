package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;

public class RunsChartLayout extends ChartLayout {

    public RunsChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, ChartLayout prevLayout) {
        super( new Config(
                layoutContext,
                chartBuilder,
                prevLayout,
                nextLayout,
                "runsChart",
                "runsChartCallback",
                "js/runs-chart.js",
                new PointRadius()
        ) );

        addNextChartButton();
    }

    @Override
    protected void pointClicked(JSONObject json) throws JSONException {
        super.pointClicked(json);
        nextChartButton.setCaption( "Run: " + json.getInt("runNumber") );
    }
}
