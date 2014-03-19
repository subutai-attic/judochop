package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.RunDao;
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

        HashSet<String> set = new HashSet<String>();
        set.add("1");
        set.add("1");

        String[] arr = set.toArray(new String[0]);

        System.out.println(arr);
    }

}
