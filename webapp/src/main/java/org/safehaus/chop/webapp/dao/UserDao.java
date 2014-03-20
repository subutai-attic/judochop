package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.webapp.dao.model.User;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class UserDao extends Dao<User> {

    private static final int MAX_RESULT_SIZE = 10000;

    @Inject
    public UserDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }

    @Override
    public boolean save( User user ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( "users", "user", user.getUsername() )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "password", user.getPassword() )
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public User get( String username ) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( "users" )
                .setTypes( "user" )
                .setQuery( termQuery( "_id", username ) )
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toUser( hits[0] ) : null;
    }

    public List<User> getList() {
        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( "users" )
                .setTypes( "user" )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        ArrayList<User> users = new ArrayList<User>();

        for ( SearchHit hit : response.getHits().hits() ) {
            users.add( toUser( hit ) );
        }

        return users;
    }

    public static User toUser( SearchHit hit ) {
        Map<String, Object> json = hit.getSource();

        return new User(
                hit.getId(),
                Util.getString( json, "password" )
        );
    }

    public boolean delete( String username ) {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete( "users", "user", username )
                .execute()
                .actionGet();

        return response.isFound();
    }

}