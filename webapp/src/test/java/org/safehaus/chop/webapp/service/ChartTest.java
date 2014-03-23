package org.safehaus.chop.webapp.service;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.*;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchResource;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.Params.Metric;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.series.Series;
import org.safehaus.chop.webapp.service.chart.builder.*;

import java.util.ArrayList;
import java.util.List;

public class ChartTest {

    @ClassRule
    public static ElasticSearchResource resource = new ElasticSearchResource();

    private ElasticSearchClient esClient = new ElasticSearchClient( resource.getConfig() );
//    private OverviewChartBuilder chartBuilder = new OverviewChartBuilder(new CommitDao(esClient), new RunDao(esClient) );
//    private RunsChartBuilder_ chartBuilder = new RunsChartBuilder_( new RunDao(esClient) );
//    private IterationsChartBuilder chartBuilder = new IterationsChartBuilder( new RunDao(esClient), new RunResultDao(esClient) );

    private CommitDao commitDao = new CommitDao(esClient);

    @Test
    public void test() {

        CommitDao commitDao = new CommitDao(esClient);
        RunDao runDao = new RunDao(esClient);
        ChartBuilder chartBuilder = new OverviewChartBuilder(commitDao, runDao);

        Params params = new Params(
                "1168044208",
                "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest",
                "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e",
                1,
                Metric.AVG,
                100,
                Params.FailureType.SUCCESS
        );

        Chart chart = chartBuilder.getChart(params);

        System.out.println(chart);
    }

}
