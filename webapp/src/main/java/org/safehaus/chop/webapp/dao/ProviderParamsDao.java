package org.safehaus.chop.webapp.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.webapp.dao.model.BasicProviderParams;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ProviderParamsDao extends Dao<ProviderParams> {

    private static final int MAX_RESULT_SIZE = 10000;
    private static final String INDEX_KEY = "providerparams";
    private static final String TYPE_KEY = "providerparam";

    @Inject
    public ProviderParamsDao ( ElasticSearchClient e ) {
        super( e );
    }


    @Override
    public boolean save( final ProviderParams pp ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( INDEX_KEY, TYPE_KEY, pp.getUsername() )
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
                        .field( "keyPairName", pp.getKeyPairName() )
                        .field( "runnerName", pp.getRunnerName() )
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    /**
     * Gets the ProviderParams that belongs to the given username
     */
    public ProviderParams getByUser( String username ) {

        SearchResponse response = elasticSearchClient.getClient()
            .prepareSearch( INDEX_KEY )
            .setTypes( TYPE_KEY )
            .setQuery( termQuery( "_id", username ) )
            .execute()
            .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? toProviderParams( hits[0] ) : null;
    }

    private static ProviderParams toProviderParams( SearchHit hit ) {

        Map<String, Object> json = hit.getSource();

        return new BasicProviderParams(
                Util.getString( json, "username" ),
                Util.getString( json, "instanceType" ),
                Util.getString( json, "availabilityZone" ),
                Util.getString( json, "accessKey" ),
                Util.getString( json, "secretKey" ),
                Util.getString( json, "imageId" ),
                Util.getString( json, "securityGroup" ),
                Util.getString( json, "keyPairName" ),
                Util.getString( json, "runnerName" )
        );
    }

    public List<ProviderParams> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( INDEX_KEY )
                .setTypes( TYPE_KEY )
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
