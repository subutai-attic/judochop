package org.safehaus.chop.webapp.view.chart.layout;

import org.safehaus.chop.webapp.service.chart.builder.ChartBuilder;
import org.safehaus.chop.webapp.view.chart.ChartLayoutContext;

class Config {

    private final ChartLayoutContext layoutContext;
    private final ChartBuilder chartBuilder;
    private final ChartLayout prevLayout;
    private final ChartLayout nextLayout;
    private final String chartId;
    private final String jsCallbackName;
    private final String chartFile;

    Config(ChartLayoutContext layoutContext, ChartBuilder chartBuilder, ChartLayout prevLayout, ChartLayout nextLayout, String chartId, String jsCallbackName, String chartFile) {
        this.layoutContext = layoutContext;
        this.chartBuilder = chartBuilder;
        this.prevLayout = prevLayout;
        this.nextLayout = nextLayout;
        this.chartId = chartId;
        this.jsCallbackName = jsCallbackName;
        this.chartFile = chartFile;
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
}
