package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.calc.iterations.IterationsCollector;
import org.safehaus.chop.webapp.service.chart.builder.IterationsChartBuilder_;
import org.safehaus.chop.webapp.service.chart.builder.RunsChartBuilder_;
import org.safehaus.chop.webapp.view.chart.format.SeriesFormat;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;
import java.util.Map;

public class IterationsChartBuilder extends ChartBuilder {

//    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
//    private RunResultDao runResultDao = InjectorFactory.getInstance(RunResultDao.class);

    private IterationsChartBuilder_ chartBuilder = InjectorFactory.getInstance(IterationsChartBuilder_.class);

    public String getChart(Params params) {

        Chart chart = chartBuilder.getChart(params);

        String chartTemplate = FileUtil.getContent("js/iterations-chart.js");
        chartTemplate = chartTemplate.replace( "$series", SeriesFormat.format( chart.getSeries() ) );

        return chartTemplate;

//        Map<String, Run> runs = runDao.getMap( params.getCommitId(), params.getRunNumber(), params.getTestName() );
//        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);
//        IterationsCollector collector = new IterationsCollector( runResults, params.getPercentile(), params.getFailureValue() );
//
//        IterationsFormat format = new IterationsFormat(collector);

//        String s = FileUtil.getContent("js/iterations-chart.js");
//        s = s.replace( "$series", format.getSeries() );
//
//        return s;
    }

}
