package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.dao.model.BasicVersion;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class RunDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private RunDao runDao;

    @Test
    public void testSave() throws Exception {

        BasicSummary summary = new BasicSummary(1, 1, 1, "TestRun");
        BasicModule module = new BasicModule("TestGroup", "TestArtifact", ""+System.currentTimeMillis(), null, null);
        BasicVersion version = new BasicVersion(UUID.randomUUID(), module);

        BasicRun run = new BasicRun(version, summary);

        boolean created = runDao.save(run);

        assertTrue(created);
    }

    @Test
    public void testGet() throws Exception {
        List<Run> runs = runDao.get();

        assertTrue(runs != null);
    }
}
