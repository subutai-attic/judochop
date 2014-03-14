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
                "cc471b502aca2791c3a068f93d15b79ff6b7b827", // commitId
                "1168044208", // moduleId
                "742e2a76a6ba161f9efb87ce58a9187e", // warMD5
                new Date() // createDate
        );

        boolean created = commitDao.save(commit);
        System.out.println(created + ": " + commit);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Commit> commits = commitDao.getAll();

        for (Commit commit : commits) {
            System.out.println(commit);
        }
    }
}
