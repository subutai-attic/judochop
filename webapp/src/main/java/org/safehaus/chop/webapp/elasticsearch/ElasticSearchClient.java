package org.safehaus.chop.webapp.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.inject.Inject;


public class ElasticSearchClient implements IElasticSearchClient {

    private Client client;

    @Inject
    private ElasticFig elasticFig;


    public ElasticSearchClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("node.name", "ChopNode")
                .build();

        // TODO take as params or from config
        String host = "192.168.1.102"; int port = 9300;
//        String host = "172.16.10.108"; int port = 9302;

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(host, port));
    }


    @Override
    public Statement apply( final Statement base, final Description description ) {
        return null;
    }


    @Override
    public Client getClient() {
        return client;
    }
}
