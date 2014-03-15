package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.OverviewAvg;
import org.safehaus.chop.webapp.service.calc.OverviewCollector;
import org.safehaus.chop.webapp.view.chart.format.OverviewFormat;

import java.util.*;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class CalcTest {

    @Inject
    private CommitDao commitDao;

    @Inject
    private RunDao runDao;

    @Test
    public void test() throws Exception {

        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
        List<Run> list = runDao.getAll();

        for (Run run : list) {
            collector.collect(run);
        }

        System.out.println(collector);

        OverviewFormat format = new OverviewFormat(collector);

        System.out.println( format.getCategories() );
        System.out.println(format.getSeries());
    }

    @Test
    public void test2() throws Exception {

        OverviewCollector collector = new OverviewCollector( commitDao.getByModule("1168044208") );
        List<Run> list = runDao.getAll();

        for (Run run : list) {
            collector.collect(run);
        }

        OverviewAvg.get(collector.getValues());

    }

}
