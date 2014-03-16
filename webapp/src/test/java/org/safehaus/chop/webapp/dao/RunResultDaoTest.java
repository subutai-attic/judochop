package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;

import java.util.List;
import java.util.Map;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunResultDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private RunResultDao runResultDao;

    @Inject
    @SuppressWarnings("unused")
    private RunDao runDao;

    @Test
    public void save() throws Exception {

        BasicRunResult runResult = new BasicRunResult("runId", 0, 0, 0, 0);

        boolean created = runResultDao.save(runResult);

        System.out.println(created + ": " + runResult);
    }

    @Test
    public void getAll() {
        List<RunResult> list = runResultDao.getAll();

        for (RunResult runResult : list) {
            if (runResult.getRunId().equals("-1683882156")) {
                System.out.println(runResult.getRunTime());
            }
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getMap() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
//        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
        Map<String, Run> runs = runDao.getMap("7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e", 2);
        Map<Run, List<RunResult>> runResults = runResultDao.getMap(runs);

        for (Run run : runResults.keySet()) {
            System.out.println(run);

            for (RunResult runResult : runResults.get(run)) {
                System.out.println("   " + runResult);
            }
        }

        System.out.println("count: " + runResults.size());
    }

}
