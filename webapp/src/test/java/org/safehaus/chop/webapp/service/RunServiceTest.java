package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.service.calc.Params;
import org.safehaus.chop.webapp.service.calc.runs.RunsCollector;
import org.safehaus.chop.webapp.view.chart.runs.RunsFormat;

import java.util.HashSet;
import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class RunServiceTest {

    @Inject
    private RunService runService;

    @Test
    public void getTestNames() {
        System.out.println(runService.getTestNames("1168044208"));
    }

    @Test
    public void test() {

        Params params = new Params(
                null,
                null,
                null,
                0,
                null,
                0,
                null
        )
                .setModuleId("moduleId")
                .setCommitId("commitId")
                .setRunNumber(1);

        System.out.println(params);
    }

}
