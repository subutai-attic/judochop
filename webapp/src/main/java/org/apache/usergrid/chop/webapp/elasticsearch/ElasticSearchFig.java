package org.apache.usergrid.chop.webapp.elasticsearch;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


@FigSingleton
public interface ElasticSearchFig extends GuicyFig {

    String CLUSTER_NAME_DEFAULT = "elasticsearch";
    String CLUSTER_NAME_KEY = "es.cluster.name";
    @Default( CLUSTER_NAME_DEFAULT )
    @Key( CLUSTER_NAME_KEY )
    String getClusterName();


    String SERVERS_KEY = "es.transport.host";
    String SERVERS_DEFAULT = "localhost";
    @Default( SERVERS_DEFAULT )
    @Key( SERVERS_KEY )
    String getTransportHost();


    String PORT_DEFAULT = "9300";
    String PORT_KEY = "es.transport.port";
    @Default( PORT_DEFAULT )
    @Key( PORT_KEY )
    int getTransportPort();


    String DATA_DIR_DEFAULT = "target/data";
    String DATA_DIR_KEY = "es.data.directory";
    @Default( DATA_DIR_DEFAULT )
    @Key( "es.data.directory" )
    String getDataDir();
}
