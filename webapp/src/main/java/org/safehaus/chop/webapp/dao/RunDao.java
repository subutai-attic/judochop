package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.statistical.StatisticalFacet;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.facet.FacetBuilders.statisticalFacet;

public class RunDao extends Dao<Run> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    public RunDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    public boolean save(Run run) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "run", run.getId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("commitId", run.getCommitId())
                                .field("runner", run.getRunner())
                                .field("runNumber", run.getRunNumber())
                                .field("testName", run.getTestName())
                                .field("chopType", run.getChopType())
                                .field("iterations", run.getIterations())
                                .field("totalTestsRun", run.getTotalTestsRun())
                                .field("threads", run.getThreads())
                                .field("delay", run.getDelay())
                                .field("time", run.getTime())
                                .field("actualTime", run.getActualTime())
                                .field("minTime", run.getMinTime())
                                .field("maxTime", run.getMaxTime())
                                .field("meanTime", run.getAvgTime())
                                .field("failures", run.getFailures())
                                .field("ignores", run.getIgnores())
                                .field("saturate", run.getSaturate())
                                // Error in ElasticSearch while saving Long - tries to store as Integer
                                //.field("startTime", run.getStartTime())
                                //.field("stopTime", run.getStopTime())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Run> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("run")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

//        System.out.println(response);

        ArrayList<Run> list = new ArrayList<Run>();

        for (SearchHit hit : response.getHits().hits()) {
            Map<String, Object> json = hit.getSource();

            BasicRun run = new BasicRun(
                    Util.getString(json, "commitId"),
                    Util.getString(json, "runner"),
                    Util.getInt(json, "runNumber"),
                    Util.getString(json, "testName")
            );

            run.copyJson(hit.getSource());

            list.add(run);
        }

        return list;
    }

    public int getNextRunNumber(String commitId) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("run")
                .setQuery( termQuery("commitId", commitId) )
                .setSize(0)
                .addFacet( statisticalFacet("stat").field("runNumber") )
                .execute().actionGet();

        StatisticalFacet facet = (StatisticalFacet) response.getFacets().facets().get(0);

        return facet.getCount() > 0 ? (int) facet.getMax()+1 : 1;
    }

}