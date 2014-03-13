package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.dao.model.BasicSummary;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class SummaryDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private SummaryDao summaryDao;

    @Test
    public void testSave() throws Exception {

        BasicSummary summary = new BasicSummary(
                "2c6e7e4863d57c9f69d32829ee3acaaee3635647",
                "ec2-107-21-150-88.compute-1.amazonaws.com",
                1,
                "org.safehaus.chop.example.DigitalWatchTest"
        );

        boolean created = summaryDao.save(summary);

        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Summary> list = summaryDao.getAll();

        for (Summary s : list) {
            System.out.println(s);
        }
    }

}
