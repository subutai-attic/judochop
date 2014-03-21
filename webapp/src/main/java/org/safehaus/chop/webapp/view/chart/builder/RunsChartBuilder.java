package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.RunsChartBuilder_;
import org.safehaus.chop.webapp.view.chart.format.SeriesFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

public class RunsChartBuilder extends ChartBuilder {

    private RunsChartBuilder_ chartBuilder = InjectorFactory.getInstance(RunsChartBuilder_.class);

    public String getChart(Params params) {

        Chart chart = chartBuilder.getChart(params);

        String chartTemplate = FileUtil.getContent("js/runs-chart.js");
        chartTemplate = chartTemplate.replace( "$series", SeriesFormat.format( chart.getSeries() ) );

        return chartTemplate;
    }

}
