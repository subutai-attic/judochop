package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.upload.ModuleFileReader;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class ModuleDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private ModuleDao moduleDao;

    @Test
    public void save() throws Exception {

        String moduleFile = "d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties";
        Module module = ModuleFileReader.read(moduleFile);
        boolean created = moduleDao.save(module);

//        assertTrue(created);
    }

    @Test
    public void testGetModules() throws Exception {

        List<Module> modules = moduleDao.getModules();

        /*for (Module m : modules) {
            System.out.println(m);
        }*/

//        assertTrue(modules.size() > 0);
    }
}
