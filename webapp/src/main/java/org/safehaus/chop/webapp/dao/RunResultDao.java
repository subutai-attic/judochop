package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class RunResultDao extends Dao<RunResult> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    public RunResultDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    public boolean save(RunResult runResult) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "runResult", runResult.getRunId())
                .setSource(
                        jsonBuilder()
                                .startObject()
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

            BasicRunResult runResult = new BasicRunResult(hit.getId());
            Map<String, Object> json = hit.getSource();

            runResult.setRunCount((Integer) json.get("runCount"));
            runResult.setRunTime((Integer) json.get("runTime"));
            runResult.setIgnoreCount((Integer) json.get("ignoreCount"));
            runResult.setFailureCount((Integer) json.get("failureCount"));

            list.add(runResult);
        }

        return list;
    }

}