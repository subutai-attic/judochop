package org.safehaus.chop.webapp.view.chart;

import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.ModuleService;
import org.safehaus.chop.webapp.service.calc.OverviewCollector;
import org.safehaus.chop.webapp.view.chart.format.OverviewFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class OverviewChart {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String get() throws Exception {

        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
        List<Run> list = runDao.getAll();
        int i = 0;

        for (Run run : list) {

            System.out.println(run);

            collector.collect(run);

            i++;
            if (i == 3) {
                break;
            }
        }

        System.out.println(collector);

        OverviewFormat format = new OverviewFormat(collector);

        String s = FileUtil.getContent("js/overview-chart.js");
        s = s.replace("$categories", format.getCategories());
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
