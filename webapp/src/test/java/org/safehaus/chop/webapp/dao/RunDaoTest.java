package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private RunDao runDao;

    @Inject
    @SuppressWarnings("unuzed")
    private CommitDao commitDao;

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
    public void getListByCommits() {

        List<Commit> commits = commitDao.getByModule("1168044208");
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";

        List<Run> list = runDao.getList(commits, testName);

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getListByCommit() {

//        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String commitId = "cc471b502aca2791c3a068f93d15b79ff6b7b827";
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";

        List<Run> list = runDao.getList(commitId, testName);

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getListByCommitAndRunNumber() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        int runNumber = 1;

        List<Run> list = runDao.getList(commitId, runNumber);

        for (Run run : list) {
            System.out.println(run);
        }

        System.out.println("count: " + list.size());
    }

    @Test
    public void getNextRunNumber() {
        System.out.println( runDao.getNextRunNumber("7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e") );
    }

    @Test
    public void get() {
        System.out.println( runDao.get("129097161") );
    }

    @Test
    public void getMapByCommitAndRunNumber() {

        String commitId = "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e";
        String testName = "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest";

        Map<String, Run> runs = runDao.getMap(commitId, 2, testName);

        for (String runId : runs.keySet()) {
            System.out.println(runId + ": " + runs.get(runId));
        }

        System.out.println("count: " + runs.size());
    }

    @Test
    public void getTestNames() {

        List<Commit> commits = commitDao.getByModule("1168044208");
        Set<String> names = runDao.getTestNames(commits);

        System.out.println(names);

    }
}
