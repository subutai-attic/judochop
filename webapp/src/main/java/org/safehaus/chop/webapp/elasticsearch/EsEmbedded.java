package org.safehaus.chop.webapp.elasticsearch;


import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.internal.InternalNode;
import org.safehaus.guicyfig.GuicyFigModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;

import static org.safehaus.chop.webapp.elasticsearch.EmbeddedUtils.newInstance;


/**
 * A class representing an embedded instance.
 */
@Singleton
public class EsEmbedded {
    private final ElasticSearchFig config;
    private InternalNode inode;
    private boolean started;


    public EsEmbedded() {
        List<Module> modules = new ArrayList<Module>( 1 );
        modules.add( new GuicyFigModule( ElasticSearchFig.class ) );
        config = Guice.createInjector( modules ).getInstance( ElasticSearchFig.class );
    }


    @Inject
    public EsEmbedded( ElasticSearchFig config ) {
        this.config = config;
    }


    public void start() {
        inode = newInstance( config );
        Client client = inode.client();
        client.admin().cluster().prepareHealth().setWaitForGreenStatus().setTimeout(
                TimeValue.timeValueSeconds( 5 ) ).execute().actionGet();
        started = true;
    }


    public void stop() {
        inode.close();
        started = false;
    }


    public ElasticSearchFig getConfig() {
        return config;
    }


    public boolean isStarted() {
        return started;
    }
}

