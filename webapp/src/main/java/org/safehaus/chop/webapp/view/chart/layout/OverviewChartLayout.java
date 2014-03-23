package org.safehaus.chop.webapp.view.chart.layout;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;
import org.safehaus.chop.webapp.view.main.Breadcrumb;

public class OverviewChartLayout extends ChartLayout {

    public OverviewChartLayout(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, Breadcrumb breadcrumb) {
        super( new Config(
                layoutContext,
                chartBuilder,
                nextLayout,
                "overviewChart",
                "overviewChartCallback",
                "js/overview-chart.js",
                new PointRadius(),
                breadcrumb
        ) );

        addNextChartButton();
    }

    @Override
    protected void pointClicked(JSONObject json) throws JSONException {
        super.pointClicked(json);

        String caption = "Commit: " + StringUtils.abbreviate(json.getString("commitId"), 10);
        nextChartButton.setCaption(caption);
    }

    @Override
    protected void handleBreadcrumb() {
        config.getBreadcrumb().setItem(this, "Overview", 0);
    }
}
