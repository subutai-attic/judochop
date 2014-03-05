package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class ModuleDaoTest {

    @Inject
    private ModuleDao moduleDao = null;

    @Test
    public void save() throws Exception {

        String version = "" + System.currentTimeMillis();
        BasicModule module = new BasicModule("TestGroup", "TestArtifact", version);

        boolean created = moduleDao.save(module);

        assertTrue(created);
    }

    @Test
    public void testGetModules() throws Exception {

        List<Module> modules = moduleDao.getModules();

        assertTrue(modules.size() > 0);
    }
}
