package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
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

        BasicSummary summary = new BasicSummary(1, 1, 1, "TestRun");
        boolean created = summaryDao.save(summary);

        assertTrue(created);
    }

    @Test
    public void testGet() throws Exception {

        List<Summary> summaries = summaryDao.getSummaries(null);

        assertTrue(summaries.size() > 0);
    }

}
