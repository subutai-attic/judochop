package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.dao.model.BasicRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class RunnerDaoTest {

    private static final String HOSTNAME = "localhost";

    @Inject
    @SuppressWarnings("unused")
    private RunnerDao runnerDao;

    @Test
    public void save() throws Exception {

        BasicRunner runner = new BasicRunner(HOSTNAME);

        boolean created = runnerDao.save(runner);

        assertTrue(created);
    }

    @Test
    public void testGet() throws Exception {

        Runner runner = runnerDao.get(HOSTNAME);

        assertTrue(runner != null);
        assertTrue(runner.getHostname().equals(HOSTNAME));

    }

    @Test
    public void testDelete() throws Exception {

        boolean deleted = runnerDao.delete(HOSTNAME);

        assertTrue(deleted);
    }

}
