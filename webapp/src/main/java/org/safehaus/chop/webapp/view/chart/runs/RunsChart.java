package org.safehaus.chop.webapp.view.chart.runs;

import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.view.util.FileUtil;

public class RunsChart {

    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String get() throws Exception {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
//        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
        RunsCollector collector = new RunsCollector( runDao.getList(commitId) );

        System.out.println(collector);

        RunsFormat format = new RunsFormat(collector);

        String s = FileUtil.getContent("js/runs-chart.js");
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
