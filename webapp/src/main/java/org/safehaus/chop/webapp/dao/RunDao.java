package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class RunDao extends Dao<Run> {

    private static final int MAX_RESULT_SIZE = 100000;

    @Inject
    public RunDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    public boolean save(Run run) throws Exception {

        String id = String.format("%s-%s-%s-%s-%s-%s",
                run.getCommitVersion().getCommitId(),
                run.getCommitVersion().getModule() .getVersion(),
                run.getSummary().getRunNumber(),
                run.getSummary().getIterations(),
                run.getSummary().getTotalTestsRun(),
                run.getSummary().getTestName()
        );

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "runResults", id)
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("testResults", "{}")
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Run> get() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("runResults")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        SearchHit[] hits = response.getHits().hits();
        ArrayList<Run> list = new ArrayList<Run>();

        for (SearchHit hit : hits) {
            Map<String, Object> json = hit.getSource();
            System.out.println(json);

            // convert json to run and add to the results list
        }

        return list;
    }

}