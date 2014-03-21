package org.safehaus.chop.webapp.view.chart.builder;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.calc.iterations.IterationsCollector;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;
import java.util.Map;

public class IterationsChartBuilder extends ChartBuilder {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);
    private RunResultDao runResultDao = InjectorFactory.getInstance(RunResultDao.class);

    public String getChart(Params params) {

        Map<String, Run> runs = runDao.getMap( params.getCommitId(), params.getRunNumber(), params.getTestName() );
        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);
        IterationsCollector collector = new IterationsCollector( runResults, params.getPercentile(), params.getFailureValue() );

        IterationsFormat format = new IterationsFormat(collector);

        String s = FileUtil.getContent("js/iterations-chart.js");
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
