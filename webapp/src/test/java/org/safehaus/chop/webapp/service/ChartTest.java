package org.safehaus.chop.webapp.service;

import org.junit.Test;
import org.safehaus.chop.webapp.dao.*;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.service.chart.Chart;
import org.safehaus.chop.webapp.service.chart.Params;
import org.safehaus.chop.webapp.service.chart.Point;
import org.safehaus.chop.webapp.service.chart.series.Series;
import org.safehaus.chop.webapp.service.chart.builder.*;

import java.util.ArrayList;

public class ChartTest {

    private ElasticSearchClient esClient = new ElasticSearchClient();
    private OverviewChartBuilder chartBuilder = new OverviewChartBuilder(new CommitDao(esClient), new RunDao(esClient) );
//    private RunsChartBuilder_ chartBuilder = new RunsChartBuilder_( new RunDao(esClient) );
//    private IterationsChartBuilder chartBuilder = new IterationsChartBuilder( new RunDao(esClient), new RunResultDao(esClient) );

    @Test
    public void test() {

        Params params = new Params(
                "1168044208",
                "org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest",
                "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e",
                0,
                "Avg Time",
                100,
                "FAILED"
//                "ALL"
        );

        Chart chart = chartBuilder.getChart(params);

        for ( Series s : chart.getSeries() ) {
            System.out.println( "--- " + s.getName() );
            for ( Point p : s.getPoints() ) {
                System.out.println(p);
            }
        }
    }

    @Test
    public void test2() {

        ArrayList<String> list = new ArrayList<String>();
        System.out.println( list.get(0) );
    }


}
