package org.safehaus.chop.webapp.view.chart.runs;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class RunsChart {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String get(String commitId, String testName, String metricType, int percentile, String failureValue) {

//        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
//        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
//        String metricType = "Avg Time";
//        int percentile = 100;
//        String failureValue = "ALL";

        List<Run> runs = runDao.getList(commitId, testName);
        RunsCollector collector = new RunsCollector(runs, metricType, percentile, failureValue);

        System.out.println(collector);

        RunsFormat format = new RunsFormat(collector);

        String s = FileUtil.getContent("js/runs-chart.js");
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
