package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class SetupDao {

    private final static Logger LOG = LoggerFactory.getLogger(SetupDao.class);

    protected IElasticSearchClient elasticSearchClient;

    @Inject
    public SetupDao(IElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public void setup() throws IOException {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( "modules", "module", "testModule" )
                .setRefresh(true)
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("groupId", "testModuleGroup" )
                                .endObject()
                )
                .execute()
                .actionGet();

        boolean created = response.isCreated();
        LOG.info("Index created: " + created);
    }

}