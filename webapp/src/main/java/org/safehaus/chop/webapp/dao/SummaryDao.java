package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
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
                .prepareIndex("modules", "summary", summary.getRunId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("chopType", summary.getChopType())
                                .field("iterations", summary.getIterations())
                                .field("totalTestsRun", summary.getTotalTestsRun())
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
            Map<String, Object> json = hit.getSource();

            summary.setChopType((String) json.get("chopType"));
//            summary.setIterations((Integer) json.get("iterations"));
//            summary.setTotalTestsRun((Integer) json.get("totalTestsRun"));

            list.add(summary);
        }

        return list;
    }

}