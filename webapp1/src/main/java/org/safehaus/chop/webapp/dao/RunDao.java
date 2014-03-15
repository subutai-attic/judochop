package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

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

        System.out.println(response);

        ArrayList<Run> list = new ArrayList<Run>();

        for (SearchHit hit : response.getHits().hits()) {
            Map<String, Object> json = hit.getSource();

            BasicRun run = new BasicRun(
                    (String) json.get("commitId"),
                    (String) json.get("runner"),
                    (Integer) json.get("runNumber"),
                    (String) json.get("testName")
            );

            list.add(run);
        }

        return list;
    }
}