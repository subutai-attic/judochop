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
    public void testSave() throws Exception {

        BasicRun run = new BasicRun(
                "testCommitId", // commitId
                "testRunner", // runner
                1, // runNumber
                "testName" // testName
        );

        boolean created = runDao.save(run);

        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Run> list = runDao.getAll();

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getNextRunNumber() throws Exception {
        System.out.println( runDao.getNextRunNumber("7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e") );
    }


}
