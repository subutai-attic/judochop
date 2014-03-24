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

import java.io.IOException;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class NoteDao extends Dao {

    public static final String DAO_INDEX_KEY = "modules";
    public static final String DAO_TYPE_KEY = "note";

    private static final int MAX_RESULT_SIZE = 10000;


    @Inject
    public NoteDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }


    public boolean save( Note note ) throws IOException {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, note.getId() )
                .setRefresh( true )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "commitId", note.getCommitId() )
                                .field( "runNumber", note.getRunNumber() )
                                .field( "text", note.getText() )
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    public Note get( String commitId, int runNumber ) {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must( termQuery( "commitId", commitId.toLowerCase() ) )
                .must( termQuery( "runNumber", runNumber ) );

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .setQuery( queryBuilder )
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().getHits();

        if ( hits.length == 0 ) {
            return null;
        }

        Map<String, Object> json = hits[ 0 ].getSource();

        return new Note(
                Util.getString( json, "moduleId" ),
                Util.getInt( json, "runNumber" ),
                Util.getString( json, "text" )
        );
    }

}