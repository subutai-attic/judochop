package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicCommit;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class CommitDaoTest {

    @Inject
    @SuppressWarnings("unuzed")
    private CommitDao commitDao;

    @Test
    public void save() throws Exception {

        Commit commit = new BasicCommit(
                // commitId
                "cc471b502aca2791c3a068f93d15b79ff6b7b827",
//                "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e",
                "1168044208", // moduleId
                "742e2a76a6ba161f9efb87ce58a9187e", // warMD5
                new Date() // createTime
        );

        boolean created = commitDao.save(commit);
        System.out.println(created + ": " + commit);
    }

    @Test
    public void testGetAll() {

        List<Commit> list = commitDao.getByModule("1168044208");

        for (Commit commit : list) {
            System.out.println(commit);
        }

        System.out.println("count: " + list.size());
    }
}
