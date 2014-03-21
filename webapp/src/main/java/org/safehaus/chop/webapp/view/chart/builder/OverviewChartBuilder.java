package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.OverviewChartBuilder_;
import org.safehaus.chop.webapp.view.chart.format.CategoriesFormat;
import org.safehaus.chop.webapp.view.chart.format.SeriesFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

public class OverviewChartBuilder extends ChartBuilder {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    // TODO SeriesBuilder
    private OverviewChartBuilder_ chartBuilder = InjectorFactory.getInstance(OverviewChartBuilder_.class);

    public String getChart(Params params) {

        Chart chart = chartBuilder.getChart(params);
        String chartTemplate = FileUtil.getContent("js/overview-chart.js");

        chartTemplate = chartTemplate.replace("$categories", CategoriesFormat.format( chart.getCategories() ) );
        chartTemplate = chartTemplate.replace( "$series", SeriesFormat.format( chart.getSeries() ) );

        return chartTemplate;
    }

}
