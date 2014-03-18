package org.safehaus.chop.webapp.view.chart.runs;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class RunsChart {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String get(String testName, String commitId, String metricType, int percentile, String failureValue) {

        List<Run> runs = runDao.getList(commitId, testName);
        RunsCollector collector = new RunsCollector(runs, metricType, percentile, failureValue);

        System.out.println(collector);

        RunsFormat format = new RunsFormat(collector);

        String s = FileUtil.getContent("js/runs-chart.js");
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
