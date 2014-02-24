package org.safehaus.chop.spi;


import java.util.Collection;


/**
 *
 */
public interface ClusterManager {
    Collection<Instance> getClusterInstances( Cluster cluster );

    void createCluster( Cluster cluster );


}
