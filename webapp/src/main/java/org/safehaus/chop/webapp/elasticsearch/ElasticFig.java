package org.safehaus.chop.webapp.elasticsearch;


import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


public interface ElasticFig extends GuicyFig {

    @Key( "elastic.search.servers" )
    String getServers();
}
