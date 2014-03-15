package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ModuleDao extends Dao<Module> {

    private static final int MAX_RESULT_SIZE = 1000;

    @Inject
    public ModuleDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    @Override
    public boolean save(Module module) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "module", module.getId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("groupId", module.getGroupId())
                                .field("artifactId", module.getArtifactId())
                                .field("version", module.getVersion())
                                .field("vcsRepoUrl", module.getVcsRepoUrl())
                                .field("testPackageBase", module.getTestPackageBase())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public Module get(String id) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("module")
                .setQuery(termQuery("_id", id))
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toModule(hits[0]) : null;
    }

    private static Module toModule(SearchHit hit) {

        Map<String, Object> json = hit.getSource();

        return new BasicModule(
                (String) json.get("groupId"),
                (String) json.get("artifactId"),
                (String) json.get("version"),
                (String) json.get("vcsRepoUrl"),
                (String) json.get("testPackageBase")
        );
    }

    public List<Module> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("module")
                .setSize(MAX_RESULT_SIZE)
                .execute()
                .actionGet();

        ArrayList<Module> list = new ArrayList<Module>();

        for (SearchHit hit : response.getHits().hits()) {
            list.add(toModule(hit));
        }

        return list;
    }
}