package org.safehaus.chop.webapp.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ElasticSearchClient implements IElasticSearchClient {

    private Client client;


    @Inject
    public ElasticSearchClient( ElasticSearchFig elasticFig) {
        Settings settings = ImmutableSettings.settingsBuilder().build();

        client = new TransportClient( settings ).addTransportAddress(
                new InetSocketTransportAddress( elasticFig.getTransportHost(), elasticFig.getTransportPort() ) );
    }


    @Override
    public Client getClient() {
        return client;
    }
}
