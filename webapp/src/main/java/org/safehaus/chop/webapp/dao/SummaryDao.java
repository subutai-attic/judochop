package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class SummaryDao extends Dao<Summary> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    public SummaryDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    public boolean save(Summary summary) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "summary", summary.getRunId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("chopType", summary.getChopType())
                                .field("iterations", summary.getIterations())
                                .field("totalTestsRun", summary.getTotalTestsRun())
                                .field("threads", summary.getThreads())
                                .field("delay", summary.getDelay())
                                .field("time", summary.getTime())
                                .field("actualTime", summary.getActualTime())
                                .field("minTime", summary.getMinTime())
                                .field("maxTime", summary.getMaxTime())
                                .field("avgTime", summary.getAvgTime())
                                .field("failures", summary.getFailures())
                                .field("ignores", summary.getIgnores())
                                .field("saturate", summary.getSaturate())
                                // Error in ElasticSearch while saving Long - tries to store as Integer
                                //.field("startTime", summary.getStartTime())
                                //.field("stopTime", summary.getStopTime())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Summary> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("summary")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        System.out.println(response);

        ArrayList<Summary> list = new ArrayList<Summary>();

        for (SearchHit hit : response.getHits().hits()) {

            BasicSummary summary = new BasicSummary(hit.getId());
            summary.copyJson(hit.getSource());

            list.add(summary);
        }

        return list;
    }

}