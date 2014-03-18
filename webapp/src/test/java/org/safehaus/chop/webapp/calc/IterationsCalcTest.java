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
import org.safehaus.chop.webapp.view.chart.iterations.IterationsFormat;

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

//        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
////        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
//        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";
//
//        Map<String, Run> runs = runDao.getMap(commitId, 2, testName);
//        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);
//
//        IterationsCollector collector = new IterationsCollector(runResults);
//
////        System.out.println(collector.getRunResults());
//
//        IterationsFormat format = new IterationsFormat(collector);
//        System.out.println( format.getSeries() );
    }


}
