package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRunner;

import java.util.Map;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunnerDaoTest {

    @Inject
    private RunnerDao runnerDao = null;

    @Test
    public void save() throws Exception {

        BasicRunner runner = new BasicRunner(
            "127.0.0.1",
            "localhost",
            8080,
            "http://localhost:8080",
            "/tmp"
        );

        boolean created = runnerDao.save(runner);
        System.out.println(created + ": " + runner);
    }

    @Test
    public void testGetRunners() throws Exception {

        Map<String, Runner> runners = runnerDao.getRunners();

        System.out.println(runners);
        System.out.println("count: " + runners.size());
    }

    @Test
    public void testGet() {
        System.out.println( runnerDao.get("-1492679439") );
    }

    @Test
    public void testDelete() {
        System.out.println( runnerDao.delete("-1492679439") );
    }

}
