package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.apache.commons.lang.ArrayUtils;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.service.chart.dto.Params;
import org.safehaus.chop.webapp.service.chart.series.OverviewSeriesBuilder;

import java.util.ArrayList;

public class ChartTest {

    private ElasticSearchClient esClient = new ElasticSearchClient();
    private OverviewSeriesBuilder seriesBuilder = new OverviewSeriesBuilder( new CommitDao(esClient), new RunDao(esClient) );

    @Test
    public void test() {

        Params params = new Params(
                "1168044208",
                "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest",
                "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e",
                1,
                "Avg Time",
                50,
                "ALL"
        );

        seriesBuilder.getSeries(params);
    }

    @Test
    public void test2() {

        double arr1[] = {1, 2};
        double arr2[] = {3, 4};
        double[] both = ArrayUtils.addAll(arr1, arr2);

        System.out.println(  ArrayUtils.toString(both) );
    }


}
