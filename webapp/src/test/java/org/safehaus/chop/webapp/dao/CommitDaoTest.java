package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.read.CommitFileReader;

import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class CommitDaoTest {

    @Inject
    private CommitDao commitDao = null;

    @Test
    public void save() throws Exception {

        String file = "d:\\temp\\chop-data\\perftest-bucket-2\\tests\\2c6e5647\\project.properties";
        Commit commit = CommitFileReader.read(file);

        boolean created = commitDao.save(commit);
        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Commit> commits = commitDao.getAll();

        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }
}
