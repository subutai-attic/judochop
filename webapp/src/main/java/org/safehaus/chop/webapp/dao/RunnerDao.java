package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.dao.model.BasicRunner;
import org.safehaus.chop.webapp.dao.model.BasicSummary;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class RunnerDao extends Dao<Runner> {

    private static final int MAX_RESULT_SIZE = 100000;

    @Inject
    public RunnerDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    @Override
    public boolean save(Runner runner) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("runners", "runner", runner.getHostname())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("hostname", runner.getHostname())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public Runner get(String hostname) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("runners")
                .setTypes("runner")
                .setQuery(termQuery("hostname", hostname))
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        System.out.println(response);

        SearchHit[] hits = response.getHits().hits();
        Map<String, Object> json = hits[0].getSource();

        Runner runner = new BasicRunner(
                (String) json.get("hostname")
        );

        return runner;
    }

    public boolean delete(String hostname) throws Exception {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete("runners", "runner", hostname)
                .execute()
                .actionGet();

        return response.isFound();
    }

}