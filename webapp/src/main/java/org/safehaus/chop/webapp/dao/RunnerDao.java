package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.apache.usergrid.chop.api.Runner;
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

    private static String getGroupId(String user, String commitId, String moduleId) {
        String id = "" + new HashCodeBuilder()
                .append( user )
                .append( commitId )
                .append( moduleId )
                .toHashCode();

        return fixTermValue( id );
    }

    public boolean save( Runner runner, String user, String commitId, String moduleId ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, runner.getUrl() )
                .setRefresh(true)
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "groupId", getGroupId( user, commitId, moduleId ) )
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


    public List<Runner> getRunners( String user, String commitId, String moduleId ) {

        SearchResponse response = getRequest( DAO_INDEX_KEY, DAO_TYPE_KEY )
                .setQuery( termQuery( "groupId", getGroupId(user, commitId, moduleId ) ) )
                .execute()
                .actionGet();

        ArrayList<Runner> runners = new ArrayList<Runner>();

        for ( SearchHit hit : response.getHits().hits() ) {
            runners.add( toRunner( hit ) );
        }

        return runners;
    }


    public boolean delete( String runnerUrl ) {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete( DAO_INDEX_KEY, DAO_TYPE_KEY, runnerUrl )
                .setRefresh( true )
                .execute()
                .actionGet();

        return response.isFound();
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