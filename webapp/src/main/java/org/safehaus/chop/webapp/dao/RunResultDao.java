package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.Util;

import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class RunResultDao extends Dao<RunResult> {

    private static final int MAX_RESULT_SIZE = 1000000;

    @Inject
    public RunResultDao( IElasticSearchClient elasticSearchClient ) {
        super( elasticSearchClient );
    }

    public boolean save( RunResult runResult ) throws Exception {

        IndexResponse response = elasticSearchClient.getClient()
                .prepareIndex( "modules", "runResult", runResult.getId() )
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

    public List<RunResult> getAll() {

        SearchResponse response = elasticSearchClient.getClient()
                .prepareSearch( "modules" )
                .setTypes( "runResult" )
                .addSort( fieldSort( "createTime" ) )
                .setSize( MAX_RESULT_SIZE )
                .execute()
                .actionGet();

        System.out.println( response );

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
                .prepareSearch( "modules" )
                .setTypes( "runResult" )
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

}