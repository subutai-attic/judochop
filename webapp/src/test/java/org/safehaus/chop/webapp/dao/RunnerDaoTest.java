package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRunner;

import java.util.List;
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
    public void delete() {

        BasicRunner runner = new BasicRunner(
                "127.0.0.1",
                "localhost",
                8080,
                "http://localhost:8080",
                "/tmp"
        );

        System.out.println( runnerDao.delete(runner) );
    }

    @Test
    public void getRunners() {
        System.out.println( runnerDao.getRunners() );
    }

}
