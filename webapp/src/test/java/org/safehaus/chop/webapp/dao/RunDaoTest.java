package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRun;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private RunDao runDao;

    @Test
    public void save() throws Exception {

        BasicRun run = new BasicRun(
                "testCommitId", // commitId
                "testRunner", // runner
                1, // runNumber
                "testName" // testName
        );

        boolean created = runDao.save(run);
        System.out.println(created + ": " + run);
    }

    @Test
    public void getAll() {

        List<Run> list = runDao.getAll();

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getByCommit() {

//        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
        List<Run> list = runDao.getByCommit(commitId);

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getNextRunNumber() throws Exception {
        System.out.println( runDao.getNextRunNumber("7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e") );
    }

    @Test
    public void get() throws Exception {
        System.out.println( runDao.get("129097161") );
    }


}
