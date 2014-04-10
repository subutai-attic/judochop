package org.apache.usergrid.chop.webapp.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ElasticSearchClient implements IElasticSearchClient {

    private Client client;
    private String host;
    private int port;
    private String clusterName;


    @Inject
    public ElasticSearchClient( ElasticSearchFig elasticFig ) {
        Settings settings = ImmutableSettings.settingsBuilder().build();

        client = new TransportClient( settings ).addTransportAddress(
                new InetSocketTransportAddress( elasticFig.getTransportHost(), elasticFig.getTransportPort() ) );
        port = elasticFig.getTransportPort();
        host = elasticFig.getTransportHost();
        clusterName = elasticFig.getClusterName();
    }


    @Override
    public Client getClient() {
        return client;
    }


    @Override
    public String getHost() {
        return host;
    }


    @Override
    public int getPort() {
        return port;
    }


    @Override
    public String getClusterName() {
        return clusterName;
    }


    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString( this );
        }
        catch ( JsonProcessingException e ) {
            e.printStackTrace();
            return "Failed serialization";
        }
    }
}
