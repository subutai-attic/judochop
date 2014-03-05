package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class SummaryDao extends Dao<Summary> {

    private static final int MAX_RESULT_SIZE = 100000;

    @Inject
    public SummaryDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    // TODO get module info from summary
    public boolean save(Summary summary) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "summary")
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("runNumber", summary.getRunNumber())
                                .field("iterations", summary.getIterations())
                                .field("totalTestsRun", summary.getTotalTestsRun())
                                .field("testName", summary.getTestName())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Summary> getSummaries(Module module) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("summary")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        SearchHit[] hits = response.getHits().hits();
        ArrayList<Summary> list = new ArrayList<Summary>();

        for (SearchHit hit : hits) {
            Map<String, Object> json = hit.getSource();

            BasicSummary summary = new BasicSummary(
                    (Integer) json.get("runNumber"),
                    (Integer) json.get("iterations"),
                    (Integer) json.get("totalTestsRun"),
                    (String) json.get("testName")
            );

            list.add(summary);
        }

        return list;
    }



}