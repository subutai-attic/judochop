package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.apache.usergrid.chop.api.Run;
import org.apache.usergrid.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class RunResultDao extends Dao {

    public static final String DAO_INDEX_KEY = "modules";
    public static final String DAO_TYPE_KEY = "runResult";

    private static final int MAX_RESULT_SIZE = 1000000;


    @Inject
    public RunResultDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }


    public boolean save( RunResult runResult ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( DAO_INDEX_KEY, DAO_TYPE_KEY, runResult.getId() )
                .setRefresh( true )
                .setSource(
                        jsonBuilder()
                                .startObject()
                                .field( "runId", runResult.getRunId() )
                                .field( "runCount", runResult.getRunCount() )
                                .field( "runTime", runResult.getRunTime() )
                                .field( "ignoreCount", runResult.getIgnoreCount() )
                                .field( "failureCount", runResult.getFailureCount() )
                                .field( "createTime", System.nanoTime() )
                                .field( "failures", runResult.getFailures() )
                                .endObject()
                )
                .execute()
                .actionGet();

        return response.isCreated();
    }

    public boolean delete( String id ) {

        DeleteResponse response = elasticSearchClient.getClient()
                .prepareDelete( DAO_INDEX_KEY, DAO_TYPE_KEY, id )
                .setRefresh( true )
                .execute()
                .actionGet();

        return response.isFound();
    }

    public List<RunResult> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .addSort( fieldSort( "createTime" ) )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        return toList( response );
    }


    private static List<RunResult> toList( SearchResponse response ) {
        ArrayList<RunResult> list = new ArrayList<RunResult>();

        for ( SearchHit hit : response.getHits().hits() ) {
            list.add( toRunResult( hit ) );
        }

        return list;
    }


    private static RunResult toRunResult( SearchHit hit ) {

        Map<String, Object> json = hit.getSource();

        return new BasicRunResult(
                hit.getId(),
                Util.getString( json, "runId" ),
                Util.getInt( json, "runCount" ),
                Util.getInt( json, "runTime" ),
                Util.getInt( json, "ignoreCount" ),
                Util.getInt( json, "failureCount" ),
                ""
        );
    }


    public Map<Run, List<RunResult>> getMap(Map<String, Run> runs) {

        String runIds = StringUtils.join(runs.keySet(), ' ');

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY )
                .setQuery( multiMatchQuery( runIds, "runId" ) )
                .addSort( fieldSort( "createTime" ) )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        HashMap<Run, List<RunResult>> runResults = new HashMap<Run, List<RunResult>>();

        for ( SearchHit hit : response.getHits().hits() ) {

            RunResult runResult = toRunResult(hit);
            Run run = runs.get( runResult.getRunId() );
            List<RunResult> list = runResults.get(run);

            if ( list == null ) {
                list = new ArrayList<RunResult>();
                runResults.put( run, list );
            }

            list.add( runResult );
        }

        return runResults;
    }


    public String getFailures( String runResultId ) {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( DAO_INDEX_KEY )
                .setTypes( DAO_TYPE_KEY)
                .setQuery( termQuery( "_id", runResultId ) )
                .execute()
                .actionGet();

        SearchHit hits[] = response.getHits().hits();

        return hits.length > 0 ? Util.getString(hits[0].getSource(), "failures") : "";
    }
}