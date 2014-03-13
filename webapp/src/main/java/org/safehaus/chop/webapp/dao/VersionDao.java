package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Version;
import org.safehaus.chop.webapp.dao.model.BasicVersion;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class VersionDao extends Dao<Version> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    private ModuleDao moduleDao = null;

    @Inject
    public VersionDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    @Override
    public boolean save(Version version) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "version", version.getId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("moduleId", version.getModule().getId())
                                .field("commitId", version.getCommitId())
                                .field("warMd5", version.getWarMd5())
                                .field("createTime", version.getCreateTime())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Version> getAll() throws Exception {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("version")
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

        System.out.println(response);

        ArrayList<Version> list = new ArrayList<Version>();

        for (SearchHit hit : response.getHits().hits()) {
            Map<String, Object> json = hit.getSource();

            BasicVersion version = new BasicVersion(
                    (String) json.get("commitId"),
                    moduleDao.get((String) json.get("moduleId")),
                    (String) json.get("warMd5"),
                    Util.toDate((String) json.get("createTime"))
            );

            list.add(version);
        }

        return list;
    }
}