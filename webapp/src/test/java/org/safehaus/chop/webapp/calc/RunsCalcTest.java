package org.safehaus.chop.webapp.calc;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.runs.*;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunsCalcTest {

    @Inject
    private RunDao runDao;

    @Test
    public void test() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
//        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
        RunsCollector collector = new RunsCollector( runDao.getList(commitId) );
        System.out.println(collector.getRuns());

//        RunsFormat format = new RunsFormat(collector);
//        System.out.println( format.getSeries() );
    }


}
