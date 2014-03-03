package org.safehaus.chop.webapp;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class ElasticSearchTest {

    public static void main(String args[]) throws Exception {

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                //.put("node.name", "TestNode")
                .build();

        TransportClient client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.0.110", 9300));

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(termQuery("user", "kimchy"))
                //.must(termQuery("name", "cpu_user"))
                //.must(termQuery("collectd_type", "memory"))
                //.must(termQuery("plugin", "memory"))
                //.must(termQuery("type_instance", "used"));
        ;

        SearchResponse response =
                client.prepareSearch("twitter")
                .setQuery(queryBuilder)
                .setSize(20)
                //.addSort("@timestamp", SortOrder.DESC)
                .execute().actionGet();

        System.out.println(response);

        /*
        GetResponse response = client.prepareGet("twitter", "tweet", "1")
                .setOperationThreaded(false)
                .execute()
                .actionGet();

        System.out.println(response.getId());
        System.out.println(response.getIndex());
        System.out.println(response.getVersion());
        System.out.println(response.getFields());
        */


        client.close();

        /*
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("192.168.0.105", 9300));

        // index, type, id
        IndexResponse response = client.prepareIndex("twitter", "tweet", "2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()
                )
                .execute()
                .actionGet();

        client.close();

        System.out.println(response.getId());
        System.out.println(response.getIndex());
        System.out.println(response.getType());
        System.out.println(response.getVersion());
        System.out.println(response.getHeaders());
        System.out.println("is created: " + response.isCreated());
        */

    }

}
