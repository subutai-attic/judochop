package org.safehaus.chop.webapp.elasticsearch;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchClient {

    private TransportClient client;

    public ElasticSearchClient() {

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.ignore_cluster_name", true)
                .put("node.name", "ChopNode")
                .build();

        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("192.168.0.110", 9300));
    }

    public TransportClient getClient() {
        return client;
    }
}
