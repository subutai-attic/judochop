package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.dao.model.BasicRunner;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class RunnerDao extends Dao<Runner> {

    @Inject
    public RunnerDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    @Override
    public boolean save(Runner runner) throws Exception {

        // TODO: key should be given as a param?
        String key = "" + new HashCodeBuilder()
                .append(runner.getIpv4Address())
                .append(runner.getHostname())
                .append(runner.getServerPort())
                .toHashCode();

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("runners", "runner", key)
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("ipv4Address", runner.getIpv4Address())
                                .field("hostname", runner.getHostname())
                                .field("serverPort", runner.getServerPort())
                                .field("url", runner.getUrl())
                                .field("tempDir", runner.getTempDir())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public Runner get(String key) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("runners")
                .setTypes("runner")
                .setQuery(termQuery("_id", key))
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toRunner(hits[0]) : null;
    }

    private static Runner toRunner(SearchHit hit) {

        Map<String, Object> json = hit.getSource();

        return new BasicRunner(
                Util.getString(json, "ipv4Address"),
                Util.getString(json, "hostname"),
                Util.getInt(json, "serverPort"),
                Util.getString(json, "url"),
                Util.getString(json, "tempDir")
        );
    }

    public Map<String, Runner> getRunners() throws Exception {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("runners")
                .setTypes("runner")
                .execute().actionGet();

        System.out.println(response);

        HashMap<String, Runner> runners = new HashMap<String, Runner> ();

        for (SearchHit hit : response.getHits().hits()) {
            runners.put(hit.getId(), toRunner(hit));
        }

        return runners;
    }

    public boolean delete(String key) {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete("runners", "runner", key)
                .execute()
                .actionGet();

        return response.isFound();
    }

}