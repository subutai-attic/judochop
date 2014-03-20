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

public class RunnerDao {

    protected IElasticSearchClient elasticSearchClient;

    @Inject
    public RunnerDao( IElasticSearchClient elasticSearchClient ) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public boolean save( Runner runner, String commitId ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( "runners", "runner", runner.getHostname() )
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
                .prepareDelete( "runners", "runner", hostname )
                .setRefresh( true )
                .execute()
                .actionGet();

        return response.isFound();
    }

    public List<Runner> getRunners( String commitId ) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( "runners" )
                .setTypes( "runner" )
                .setQuery( termQuery( "commitId", commitId.toLowerCase() ) )
                .execute()
                .actionGet();

        System.out.println( response );

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