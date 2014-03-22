package org.safehaus.chop.webapp.view.chart.layout;

import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;

public class OverviewChartLayout extends ChartLayout {

    public OverviewChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout prevLayout, ChartLayout nextLayout) {
        super( new Config(
                layoutContext,
                chartBuilder,
                prevLayout,
                nextLayout,
                "overviewChart",
                "overviewChartCallback",
                "js/overview-chart.js",
                new PointRadius()
        ) );

        addNextChartButton();
    }

    @Override
    protected void pointClicked(JSONObject json) throws JSONException {
        super.pointClicked(json);
        nextChartButton.setCaption( "Commit: " + json.getString("commitId") );
    }
}
