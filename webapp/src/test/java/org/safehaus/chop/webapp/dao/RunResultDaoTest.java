package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.dao.model.BasicSummary;

import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(org.safehaus.chop.webapp.Module.class)
public class RunResultDaoTest {

    @Inject
    @SuppressWarnings("unused")
    private RunResultDao dao;

    @Test
    public void testSave() throws Exception {

        BasicRunResult runResult = new BasicRunResult("runId", 0, 0, 0, 0);

        boolean created = dao.save(runResult);

        System.out.println(created);
    }

    @Test
    public void testGetAll() throws Exception {

        List<RunResult> list = dao.getAll();

        for (RunResult r : list) {
            System.out.println(r);
        }

        System.out.println("count: " + list.size());
    }

}
