package org.safehaus.chop.webapp.view.chart.overview;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.InjectorFactory;
import org.safehaus.chop.webapp.service.calc.overview.OverviewCollector;
import org.safehaus.chop.webapp.view.chart.overview.OverviewFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;

public class OverviewChart {

    private CommitDao commitDao = InjectorFactory.getInstance(CommitDao.class);
    private RunDao runDao = InjectorFactory.getInstance(RunDao.class);

    public String get() throws Exception {

        List<Commit> commits = commitDao.getByModule("1168044208");
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";

        List<Run> list = runDao.getList(commits, testName);

        OverviewCollector collector = new OverviewCollector( commits );

        for (Run run : list) {
            collector.collect(run);
        }

        System.out.println(collector);

        OverviewFormat format = new OverviewFormat(collector);

        String s = FileUtil.getContent("js/overview-chart.js");
        s = s.replace( "$categories", format.getCategories() );
        s = s.replace( "$series", format.getSeries() );

        return s;
    }

}
