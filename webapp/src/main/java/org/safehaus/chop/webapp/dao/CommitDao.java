package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class CommitDao extends Dao {

    public static final String DAO_INDEX_KEY = "modules";
    public static final String DAO_TYPE_KEY = "commit";

    private static final int MAX_RESULT_SIZE = 10000;


    @Inject
    public CommitDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }


    public boolean save( Commit commit ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( "modules", "commit", commit.getId() )
                .setRefresh( true )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field("moduleId", commit.getModuleId())
                                .field("md5", commit.getMd5())
                                .field("createTime", commit.getCreateTime())
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    public List<Commit> getByModule( String moduleId ) {

        LOG.info( "moduleId: {}", moduleId );

        SearchResponse response = getRequest( DAO_INDEX_KEY, DAO_TYPE_KEY )
                .setQuery( termQuery( "moduleId", moduleId ) )
                .addSort( fieldSort( "createTime" ) )
                .setSize( MAX_RESULT_SIZE )
                .execute().actionGet();

        ArrayList<Commit> commits = new ArrayList<Commit>();

        for ( SearchHit hit : response.getHits().hits() ) {
            Map<String, Object> json = hit.getSource();

            BasicCommit commit = new BasicCommit(
                    hit.getId(),
                    Util.getString( json, "moduleId" ),
                    Util.getString( json, "md5" ),
                    Util.toDate( Util.getString( json, "createTime" ) ),
                    Util.getString( json, "runnerPath" )
            );

            commits.add( commit );
        }

        LOG.info( "commits: {}", commits.size() );

        return commits;
    }
}