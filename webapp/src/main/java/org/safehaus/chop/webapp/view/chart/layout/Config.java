package org.safehaus.chop.webapp.view.chart.layout;

import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;
import org.safehaus.chop.webapp.view.main.Breadcrumb;

class Config {

    private final ChartLayoutContext layoutContext;
    private final ChartBuilder chartBuilder;
    private final ChartLayout nextLayout;
    private final String chartId;
    private final String jsCallbackName;
    private final String chartFile;
    private final PointRadius pointRadius;
    private final Breadcrumb breadcrumb;

    Config(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout nextLayout, String chartId, String jsCallbackName, String chartFile,
           PointRadius pointRadius, Breadcrumb breadcrumb) {
        this.layoutContext = layoutContext;
        this.chartBuilder = chartBuilder;
        this.nextLayout = nextLayout;
        this.chartId = chartId;
        this.jsCallbackName = jsCallbackName;
        this.chartFile = chartFile;
        this.pointRadius = pointRadius;
        this.breadcrumb = breadcrumb;
    }

    ChartLayoutContext getLayoutContext() {
        return layoutContext;
    }

    ChartBuilder getChartBuilder() {
        return chartBuilder;
    }

    ChartLayout getNextLayout() {
        return nextLayout;
    }

    String getChartId() {
        return chartId;
    }

    String getJsCallbackName() {
        return jsCallbackName;
    }

    String getChartFile() {
        return chartFile;
    }

    PointRadius getPointRadius() {
        return pointRadius;
    }

    Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }
}
