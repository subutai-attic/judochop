package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.model.BasicCommit;

import java.util.Date;
import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class CommitDaoTest {

    @Inject
    private CommitDao commitDao = null;

    @Test
    public void save() throws Exception {

        Commit commit = new BasicCommit(
                "2c6e7e4863d57c9f69d32829ee3acaaee3635647",
                "1168044208",
                "742e2a76a6ba161f9efb87ce58a9187e",
                new Date()
        );

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
