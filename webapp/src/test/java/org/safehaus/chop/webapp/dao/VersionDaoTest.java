package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Version;
import org.safehaus.chop.webapp.read.ModuleFileReader;
import org.safehaus.chop.webapp.read.VersionFileReader;

import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class VersionDaoTest {

    @Inject
    private VersionDao versionDao = null;

    @Test
    public void save() throws Exception {

        String file = "d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties";
        Version version = VersionFileReader.read(file);

        boolean created = versionDao.save(version);
        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Version> versions = versionDao.getAll();

        for (Version v : versions) {
            System.out.println(v);
        }
    }
}
