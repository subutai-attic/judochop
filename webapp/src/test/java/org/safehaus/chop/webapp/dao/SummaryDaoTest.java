package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicSummary;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class SummaryDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private SummaryDao summaryDao;

    @Test
    public void testSave() throws Exception {

        BasicSummary summary = new BasicSummary("testRunId");

        boolean created = summaryDao.save(summary);

        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<Summary> list = summaryDao.getAll();

        for (Summary s : list) {
            System.out.println(s);
        }

        System.out.println("count: " + list.size());
    }

}
