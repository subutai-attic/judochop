package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.MetricCollector;
import org.safehaus.chop.webapp.service.metric.Metric;

import java.util.*;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class CalcTest {

    @Inject
    private RunDao runDao;

    @Test
    public void test() throws Exception {

        List<Run> list = runDao.getAll();
        MetricCollector collector = new MetricCollector();
        int i = 0;

        for (Run run : list) {

            System.out.println(run);

            collector.collect(run);

            i++;
            if (i == 3) {
//                break;
            }
        }

        System.out.println(collector);
    }
}
