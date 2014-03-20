package org.safehaus.chop.webapp.elasticsearch;


import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;


public class SimpleESTest {

    private static final String TEST_JSON = "{ \"message\": \"Hello World\" }";

//
//    @Rule
//    public ElasticSearchResource resource = new ElasticSearchResource();
//
//
//    @Test
//    public void testES() {
//        Client client = resource.getClient();
//        client.prepareIndex( "messages", "message", "motd" )
//              .setSource( TEST_JSON ).execute().actionGet();
//
//        client.prepareIndex( "messages", "message", "motd2" )
//                      .setSource( TEST_JSON ).execute().actionGet();
//
//        IndexResponse response = client.prepareIndex( "messages", "message", "motd" )
//                      .setSource( TEST_JSON ).execute().actionGet();
//
//
//        assertNotNull( response );
//        assertTrue( response.isCreated() );
//
//        SearchResponse searchResponse = client
//                .prepareSearch( "messages" )
//                .setTypes( "message" )
//                .setQuery( termQuery( "_id", "motd" ) )
//                .execute()
//                .actionGet();
//
//        SearchHit[] hits = searchResponse.getHits().hits();
//        assertNotNull( hits );
//        assertTrue( hits.length > 0 );
//    }
}
