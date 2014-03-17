package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.overview.OverviewCollector;
import org.safehaus.chop.webapp.view.chart.overview.OverviewFormat;

import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class OverviewCalcTest {

    @Inject
    private CommitDao commitDao;

    @Inject
    private RunDao runDao;

    @Test
    public void test() throws Exception {
//
//        List<Commit> commits = commitDao.getByModule("1168044208");
//        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
//
//        OverviewCollector collector = new OverviewCollector( commits, 100 );
//
////        List<Run> list = runDao.getAll();
//        List<Run> list = runDao.getList(commits, testName);
//
//        for (Run run : list) {
//            collector.collect(run);
//        }
//
//        System.out.println(collector);
//
//        OverviewFormat format = new OverviewFormat(collector);
//
//        System.out.println( format.getCategories() );
//        System.out.println(format.getSeries());
    }

    @Test
    public void test2() throws Exception {

//        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
//        List<Run> list = runDao.getAll();
//
//        for (Run run : list) {
//            collector.collect(run);
//        }
//
//        System.out.println(collector);
    }

    @Test
    public void test3() throws Exception {

        double[] arr = {3, 2, 3, 3, 3};
        double p = new DescriptiveStatistics(arr).getPercentile(50);

        System.out.println(p);

    }
}
