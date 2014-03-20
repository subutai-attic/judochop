package org.safehaus.chop.webapp.elasticsearch;


import java.io.File;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticSearchResource extends ExternalResource implements IElasticSearchClient {

    private Node node;
    private Client client;

    private static Logger LOG = LoggerFactory.getLogger( ElasticSearchResource.class );


    protected void before() throws Throwable {
        super.before();

        LOG.info( "ElasticSearchResource external resource is being created..." );

        node = NodeBuilder.nodeBuilder()
                          .local( true )
                          .settings( buildNodeSettings() )
                          .node();


        // Get a client
        client = node.client();

        // Wait for Yellow status
        client.admin().cluster()
                      .prepareHealth()
                      .setWaitForGreenStatus()
                      .setTimeout( TimeValue.timeValueMinutes( 1 ) )
                      .execute()
                      .actionGet();

    }


    @Override
    protected void after() {
        super.after();

        LOG.info( "ElasticSearchResource is being closed..." );

        if ( client != null ) {

            client.close();
        }

        if ( ( node != null ) && ( ! node.isClosed() ) ) {
            node.stop();
            node.close();

            FileSystemUtils.deleteRecursively( new File( "./target/elasticsearch-test/" ), true );

        }
    }


    @Override
    public Client getClient() {
        return client;
    }


    protected Settings buildNodeSettings() {
        // Build settings
        ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder()
                .put( "node.name", "ChopNode" )
                .put( "node.data", true )
                .put( "cluster.name", "cluster-test-localhost" )
                .put( "index.store.type", "memory" )
                .put( "index.store.fs.memory.enabled", "true" )
                .put( "gateway.type", "none" )
                .put( "path.data", "./target/elasticsearch-test/data" )
                .put( "path.work", "./target/elasticsearch-test/work" )
                .put( "path.logs", "./target/elasticsearch-test/logs" )
                .put( "index.number_of_shards", "1" )
                .put( "index.number_of_replicas", "0" )
                .put( "cluster.routing.schedule", "50ms" )
                .put( "node.local", true );

        return builder.build();
    }
}
