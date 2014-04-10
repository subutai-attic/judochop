package org.safehaus.chop.webapp.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.apache.usergrid.chop.api.ProviderParams;
import org.safehaus.chop.webapp.dao.model.BasicProviderParams;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ProviderParamsDao extends Dao {

    public static final String DAO_INDEX_KEY = "providerparams";
    public static final String DAO_TYPE_KEY = "providerparam";

    private static final int MAX_RESULT_SIZE = 10000;


    @Inject
    public ProviderParamsDao ( IElasticSearchClient e ) {
        super( e );
    }


    public boolean save( final ProviderParams pp ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, pp.getUsername() )
                .setRefresh( true )
                .setSource(
                    jsonBuilder()
                        .startObject()
                        .field( "username", pp.getUsername() )
                        .field( "instanceType", pp.getInstanceType() )
                        .field( "availabilityZone", pp.getAvailabilityZone() )
                        .field( "accessKey", pp.getAccessKey() )
                        .field( "secretKey", pp.getSecretKey() )
                        .field( "imageId", pp.getImageId() )
                        .field( "securityGroup", pp.getSecurityGroup() )
                        .field( "runnerName", pp.getRunnerName() )
                        .field( "keys", pp.getKeys().toString() )
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    /**
     * Gets the ProviderParams that belongs to the given username
     */
    public ProviderParams getByUser( String username ) {

        SearchResponse response = getRequest( DAO_INDEX_KEY, DAO_TYPE_KEY )
            .setQuery( termQuery( "_id", username ) )
            .execute()
            .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toProviderParams( hits[0] ) : null;
    }


    private static ProviderParams toProviderParams( SearchHit hit ) {

        Map<String, Object> json = hit.getSource();

        BasicProviderParams params = new BasicProviderParams(
                Util.getString( json, "username" ),
                Util.getString( json, "instanceType" ),
                Util.getString( json, "availabilityZone" ),
                Util.getString( json, "accessKey" ),
                Util.getString( json, "secretKey" ),
                Util.getString( json, "imageId" ),
                Util.getString( json, "securityGroup" ),
                Util.getString( json, "runnerName" )
        );

        params.setKeys( Util.getMap( json, "keys" ) );

        return params;
    }

    public List<ProviderParams> getAll() {

        SearchResponse response = getRequest( DAO_INDEX_KEY, DAO_TYPE_KEY )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        ArrayList<ProviderParams> list = new ArrayList<ProviderParams>();

        for ( SearchHit hit : response.getHits().hits() ) {
            list.add( toProviderParams( hit ) );
        }

        return list;
    }
}
