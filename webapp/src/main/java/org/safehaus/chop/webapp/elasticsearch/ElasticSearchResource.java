package org.safehaus.chop.webapp.elasticsearch;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.transport.netty.NettyTransport;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Module;

import static org.safehaus.chop.webapp.elasticsearch.ElasticSearchFig.DATA_DIR_KEY;


public class ElasticSearchResource implements TestRule {
    private static final Logger LOG = LoggerFactory.getLogger( ElasticSearchResource.class );

    private InternalNode node;
    private final ElasticSearchFig config;


    public ElasticSearchResource() {
        List<Module> modules = new ArrayList<Module>( 1 );
        modules.add( new GuicyFigModule( ElasticSearchFig.class ) );
        config = Guice.createInjector( modules ).getInstance( ElasticSearchFig.class );

    }


    protected void before() throws Exception {
        node = ( InternalNode )
                NodeBuilder.nodeBuilder().settings( getSettings() )
                           .data( true )
                           .clusterName( config.getClusterName() )
                           .node();
        extractTransportInfo();
    }


    private Settings getSettings() throws IOException {
        String dataDir;
        InputStream in = getClass().getClassLoader().getResourceAsStream( "ElasticSearchResource.properties" );

        if ( in != null ) {
            Properties props = new Properties();
            props.load( in );
            dataDir = props.getProperty( DATA_DIR_KEY );

            if ( ! dataDir.equals( config.getDataDir() ) ) {
                config.bypass( DATA_DIR_KEY, dataDir );
            }

            in.close();
        }
        else {
            dataDir = config.getDataDir();
        }

        return ImmutableSettings.settingsBuilder()
                .put( "path.data", dataDir )
                .build();
    }


    /**
     * This is a hack for now. Can't easily figure out how to get the transport information.
     */
    private void extractTransportInfo() {
        TransportAddress ta = getTransportAddress();

        LOG.info( "ta = {}", ta.toString() );

        String[] strings = ta.toString().split( ":" );

        String transportHost = strings[0].substring( 6 );
        LOG.info( "host = {}", transportHost );
        String transportPortStr = strings[1].substring( 0, strings[1].length() - 1 );

        config.bypass( ElasticSearchFig.PORT_KEY, transportPortStr );
        config.bypass( ElasticSearchFig.SERVERS_KEY, transportHost );
    }


    TransportAddress getTransportAddress() {
        return node.injector()
                   .getInstance( NettyTransport.class )
                   .boundAddress()
                   .publishAddress();
    }


    public Client getClient() {
        // Get a client
        Client client = node.client();

        // Wait for Yellow status
        client.admin().cluster()
                      .prepareHealth()
                      .setWaitForGreenStatus()
                      .setTimeout( TimeValue.timeValueMinutes( 1 ) )
                      .execute()
                      .actionGet();

        return client;
    }


    public ElasticSearchFig getConfig() {
        return config;
    }


    protected void after() throws Exception {
        if ( ! node.isClosed() ) {
            node.close();
        }
    }


    private Statement statement( final Statement base ) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                }
                finally {
                    after();
                }
            }
        };
    }


    @Override
    public Statement apply( final Statement base, final Description description ) {
        return statement( base );
    }
}
