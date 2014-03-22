package org.safehaus.chop.webapp.view.chart.layout;

import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;
import org.safehaus.chop.webapp.view.chart.format.PointRadius;

class Config {

    private final ChartLayoutContext layoutContext;
    private final ChartBuilder chartBuilder;
    private final ChartLayout prevLayout;
    private final ChartLayout nextLayout;
    private final String chartId;
    private final String jsCallbackName;
    private final String chartFile;
    private final PointRadius pointRadius;

    Config(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout prevLayout, ChartLayout nextLayout, String chartId, String jsCallbackName, String chartFile,
           PointRadius pointRadius) {
        this.layoutContext = layoutContext;
        this.chartBuilder = chartBuilder;
        this.prevLayout = prevLayout;
        this.nextLayout = nextLayout;
        this.chartId = chartId;
        this.jsCallbackName = jsCallbackName;
        this.chartFile = chartFile;
        this.pointRadius = pointRadius;
    }

    ChartLayoutContext getLayoutContext() {
        return layoutContext;
    }

    ChartBuilder getChartBuilder() {
        return chartBuilder;
    }

    ChartLayout getPrevLayout() {
        return prevLayout;
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
}
