package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.calc.runs.RunsCollector;
import org.safehaus.chop.webapp.service.chart.builder.OverviewChartBuilder_;
import org.safehaus.chop.webapp.service.chart.builder.RunsChartBuilder_;
import org.safehaus.chop.webapp.view.chart.format.CategoriesFormat;
import org.safehaus.chop.webapp.view.chart.format.SeriesFormat;
import org.safehaus.chop.webapp.view.chart.runs.RunsFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class RunsChartBuilder extends ChartBuilder {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    private RunsChartBuilder_ chartBuilder = InjectorFactory.getInstance(RunsChartBuilder_.class);

    public String getChart(Params params) {

        Chart chart = chartBuilder.getChart(params);

        String chartTemplate = FileUtil.getContent("js/runs-chart.js");
//        chartTemplate = chartTemplate.replace("$categories", CategoriesFormat.format(chart.getCategories()) );
        chartTemplate = chartTemplate.replace( "$series", SeriesFormat.format( chart.getSeries() ) );

        return chartTemplate;

        //        String s = FileUtil.getContent("js/runs-chart.js");
//        s = s.replace( "$series", format.getSeries() );

//        List<Run> runs = runDao.getList(params.getCommitId(), params.getTestName());
//        RunsCollector collector = new RunsCollector(runs, params.getMetricType(), params.getPercentile(), params.getFailureValue());
//
//        System.out.println(collector);
//
//        RunsFormat format = new RunsFormat(collector);
//
//        String s = FileUtil.getContent("js/runs-chart.js");
//        s = s.replace( "$series", format.getSeries() );
//
//        return s;
    }

}
