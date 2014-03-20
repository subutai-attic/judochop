package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.service.chart.dto.Params;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class DataServiceTest {

    @Inject
    private DataService dataService;

    @Test
    public void getTestNames() {
        System.out.println(dataService.getTestNames("1168044208"));
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
