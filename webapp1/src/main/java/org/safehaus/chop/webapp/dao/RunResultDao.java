package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class RunResultDao extends Dao<RunResult> {

    private static final int MAX_RESULT_SIZE = 1000000;

    @Inject
    public RunResultDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    public boolean save(RunResult runResult) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "runResult")
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("runId", runResult.getRunId())
                                .field("runCount", runResult.getRunCount())
                                .field("runTime", runResult.getRunTime())
                                .field("ignoreCount", runResult.getIgnoreCount())
                                .field("failureCount", runResult.getFailureCount())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<RunResult> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("runResult")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        System.out.println(response);

        ArrayList<RunResult> list = new ArrayList<RunResult>();

        for (SearchHit hit : response.getHits().hits()) {

            Map<String, Object> json = hit.getSource();

            BasicRunResult runResult = new BasicRunResult(
                    Util.getString(json, "runId"),
                    Util.getInt(json, "runCount"),
                    Util.getInt(json, "runTime"),
                    Util.getInt(json, "ignoreCount"),
                    Util.getInt(json, "failureCount")
            );

            list.add(runResult);
        }

        return list;
    }

}