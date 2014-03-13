package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.read.ModuleFileReader;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class ModuleDaoTest {

    @Inject
    private ModuleDao moduleDao = null;

    @Test
    public void save() throws Exception {

        String moduleFile = "d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties";
        Module module = ModuleFileReader.read(moduleFile);
        boolean created = moduleDao.save(module);

//        assertTrue(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Module> modules = moduleDao.getAll();

        for (Module m : modules) {
            System.out.println(m);
        }
    }

    @Test
    public void testGetById() {
        System.out.println( moduleDao.get("1168044208") );
    }

}
