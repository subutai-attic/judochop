package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class CommitDao extends Dao<Commit> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    public CommitDao(ElasticSearchClient elasticSearchClient) {
        super(elasticSearchClient);
    }

    @Override
    public boolean save(Commit commit) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex("modules", "commit", commit.getId())
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("moduleId", commit.getModuleId())
                                .field("warMd5", commit.getWarMd5())
                                .field("createTime", commit.getCreateTime())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public List<Commit> getByModule(String moduleId) throws Exception {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch("modules")
                .setTypes("commit")
                .setQuery( termQuery("moduleId", moduleId) )
                .addSort( fieldSort("createTime") )
                .setSize(MAX_RESULT_SIZE)
                .execute().actionGet();

//        System.out.println(response);

        ArrayList<Commit> list = new ArrayList<Commit>();

        for (SearchHit hit : response.getHits().hits()) {
            Map<String, Object> json = hit.getSource();

            BasicCommit commit = new BasicCommit(
                    hit.getId(),
                    Util.getString(json, "moduleId"),
                    Util.getString(json, "warMd5"),
                    Util.toDate(Util.getString(json, "createTime"))
            );

            list.add(commit);
        }

        return list;
    }
}