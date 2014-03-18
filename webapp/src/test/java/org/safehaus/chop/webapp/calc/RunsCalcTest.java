package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.runs.*;
import org.safehaus.chop.webapp.view.chart.runs.RunsFormat;

import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunsCalcTest {

    @Inject
    private RunDao runDao;

    @Test
    public void test() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        String metricType = "Min Time";
        int percentile = 100;
        String failureValue = "ALL";

        List<Run> runs = runDao.getList(commitId, testName);

//        for (Run run: runs) {
//            if (run.getRunNumber() == 1) {
//                System.out.println(run);
//            }
//        }

        RunsCollector collector = new RunsCollector(runs, metricType, percentile, failureValue);
        System.out.println(collector.getRuns());

        RunsFormat format = new RunsFormat(collector);
        System.out.println( format.getSeries() );
    }


}
