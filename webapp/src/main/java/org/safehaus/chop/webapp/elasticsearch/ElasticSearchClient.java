package org.safehaus.chop.webapp.elasticsearch;

import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchClient {

    private AbstractClient client;

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

    public AbstractClient getClient() {
        return client;
    }
}
