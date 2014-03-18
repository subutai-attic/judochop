package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.service.calc.iterations.IterationsCollector;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsChart;
import org.safehaus.chop.webapp.view.chart.iterations.IterationsFormat;
import org.safehaus.chop.webapp.view.util.FileUtil;

import java.util.List;
import java.util.Map;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class IterationsCalcTest {

    @Inject
    private RunResultDao runResultDao;

    @Inject
    private RunDao runDao;

    @Test
    public void test() {

        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        int runNumber = 7;
        int percentile = 90;
        String failureValue = "ALL";

        Map<String, Run> runs = runDao.getMap(commitId, runNumber, testName);
        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);

        IterationsCollector collector = new IterationsCollector(runResults, percentile, failureValue);
//        IterationsFormat format = new IterationsFormat(collector);

//        System.out.println(collector.getRunResults());

//        Map<String, Run> runs = runDao.getMap(commitId, runNumber, testName);
//        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);
//
//        IterationsCollector collector = new IterationsCollector(runResults, percentile, failureValue);
////        Map<Run, List<RunResult>> iterations = collector.getRunResults();
//
//        IterationsFormat format = new IterationsFormat(collector);
//
//        System.out.println( format.getSeries() );

//        for (Run run : iterations.keySet()) {
//            System.out.println(run.getRunner());
//        }


////        for (Run run : runResults.keySet()) {
//        for (Run run : iterations.keySet()) {
//            if (run.getId().equals("-2043597735")) {
////                for (RunResult runResult : runResults.get(run)) {
//                for (RunResult runResult : iterations.get(run)) {
//                    System.out.println( runResult );
//                }
//            }
//        }

//        IterationsFormat format = new IterationsFormat(collector);
    }

}
