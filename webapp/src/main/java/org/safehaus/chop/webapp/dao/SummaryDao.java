package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

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
                .prepareIndex("modules", "summary", summary.getId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("commitId", summary.getCommitId())
                                .field("runner", summary.getRunner())
                                .field("runNumber", summary.getRunNumber())
                                .field("testName", summary.getTestName())
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

//        System.out.println(response);

        ArrayList<Summary> list = new ArrayList<Summary>();

        for (SearchHit hit : response.getHits().hits()) {
            Map<String, Object> json = hit.getSource();

            BasicSummary summary = new BasicSummary(
                    (String) json.get("commitId"),
                    (String) json.get("runner"),
                    (Integer) json.get("runNumber"),
                    (String) json.get("testName")
            );

            list.add(summary);
        }

        return list;
    }

}