package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.dao.model.BasicRunner;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class RunnerDao extends Dao {

    public static final String DAO_INDEX_KEY = "runners";
    public static final String DAO_TYPE_KEY = "runner";


    @Inject
    public RunnerDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }


    public boolean save( Runner runner, String commitId ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, runner.getHostname() )
                .setRefresh( true )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "commitId", commitId )
                                .field( "ipv4Address", runner.getIpv4Address() )
                                .field( "hostname", runner.getHostname() )
                                .field( "serverPort", runner.getServerPort() )
                                .field( "url", runner.getUrl() )
                                .field( "tempDir", runner.getTempDir() )
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }


    public boolean delete( String hostname ) {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete( DAO_INDEX_KEY, DAO_TYPE_KEY, hostname )
                .setRefresh( true )
                .execute()
                .actionGet();

        return response.isFound();
    }


    public List<Runner> getRunners( String commitId ) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .setQuery( termQuery( "commitId", commitId.toLowerCase() ) )
                .execute()
                .actionGet();

        ArrayList<Runner> runners = new ArrayList<Runner> ();

        for ( SearchHit hit : response.getHits().hits() ) {
            runners.add( toRunner( hit ) );
        }

        return runners;
    }


    private static Runner toRunner( SearchHit hit ) {

        Map<String, Object> json = hit.getSource();

        return new BasicRunner(
                Util.getString( json, "ipv4Address" ),
                Util.getString( json, "hostname" ),
                Util.getInt( json, "serverPort" ),
                Util.getString( json, "url" ),
                Util.getString( json, "tempDir" )
        );
    }

}