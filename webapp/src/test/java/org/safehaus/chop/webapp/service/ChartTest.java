package org.safehaus.chop.webapp.service;

import org.junit.Test;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.builder.OverviewChartBuilder_;

import java.util.HashSet;
import java.util.Set;

public class ChartTest {

    private ElasticSearchClient esClient = new ElasticSearchClient();
    private OverviewChartBuilder_ seriesBuilder = new OverviewChartBuilder_( new CommitDao(esClient), new RunDao(esClient) );

    @Test
    public void test() {

        Params params = new Params(
                "1168044208",
                "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest",
                "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e",
                1,
                "Avg Time",
                100,
                "ALL"
        );

        Chart chart = seriesBuilder.getChart(params);
        System.out.println(chart.getCategories());
//
//        for (Series s : list) {
//            System.out.println(s);
//        }

    }

    @Test
    public void test2() {

        Set<String> set = new HashSet<String>();
        set.add("1");

        System.out.println(set);
    }


}
