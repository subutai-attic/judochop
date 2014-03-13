package org.safehaus.chop.webapp.read;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import static org.elasticsearch.index.query.QueryBuilders.queryString;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;

public class Test {

    public static void main(String args[]) {

        TransportClient client = getClient();
        //SearchRequestBuilder requestBuilder = client.prepareSearch("logstash*");

        QueryBuilder qb = QueryBuilders.multiMatchQuery(
                "kimchy askat",     // Text you are looking for
                "user"
//                ,"message"           // Fields you query on
        );

        SearchResponse response = client.prepareSearch(/*"index1", "index2"*/)
//                .setTypes("type1", "type2")
//                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(QueryBuilders.termQuery("user", "kimchy"))             // Query
                .setQuery(qb)             // Query
//                .setFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
//                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        System.out.println(response);

        /*
        BoolQueryBuilder queryBuilders =  QueryBuilders.boolQuery().must(queryString("log_host:*"));

        requestBuilder.setQuery(queryBuilders);
        requestBuilder.setFilter(FilterBuilders.rangeFilter("@timestamp").from(hoursEarlier).to(currentTimestamp));
        requestBuilder.setFrom(0).setSize(100);

        SearchResponse searchResponse = requestBuilder.execute().actionGet();
        */

//        SearchResponse searchResponse = client.prepareSearch().execute().actionGet();
//        System.out.println(">> " + searchResponse);

        //logger.log(Level.INFO, "Full Query: " + requestBuilder.toString());
        //logger.log(Level.INFO, "Result Count: " + searchResponse.getHits().getHits().length);

        /*for (int i = 0; i< searchResponse.getHits().getHits().length; i++) {

            String temp = (String) searchResponse.getHits().getHits()[i].getSource().get("log_host");

            if (temp.contains(":")) {
                temp = temp.substring(temp.indexOf(":")+1,temp.length());
                System.out.println(temp);
            }
        }*/

        client.close();

        //logger.log(Level.INFO, "Updated host list: ");

        /*for(int i = 0; i < hosts.size(); i++) {
            System.out.println(hosts.get(i));
        }*/

    }

    private static TransportClient getClient() {

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("node.name", "ChopNode")
                .build();

        // TODO take as params or from config
        String host = "192.168.1.102"; int port = 9300;
//        String host = "172.16.10.108"; int port = 9302;

        return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(host, port));
    }

    /*public static void main_(String args[]) {

        Timestamp currentTime = Timestamp.getCurrentTimestamp();
        Timestamp hoursEarlier = Timestamp.getHoursEarlier(currentTime, 5);

//        Settings settings = ImmutableSettings.settingsBuilder().put("elasticsearch", "localtestsearch").build();
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();

        TransportClient client = new TransportClient(settings);
        client = client.addTransportAddress(new InetSocketTransportAddress("172.16.10.103", 930));

        SearchRequestBuilder requestBuilder = client.prepareSearch("logstash*");
        requestBuilder.setFilter(FilterBuilders.rangeFilter("@timestamp").from(currentTime).to(hoursEarlier));
        requestBuilder.addSort(fieldSort("@timestamp"));
        requestBuilder.setFrom(-1).setSize(200000);
        requestBuilder.setQuery(getAllMetricsQuery());

        SearchResponse response = requestBuilder.execute().actionGet();

        System.out.println("test");
    }*/
}
