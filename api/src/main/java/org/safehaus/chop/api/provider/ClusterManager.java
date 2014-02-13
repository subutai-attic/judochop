package org.safehaus.chop.api.provider;


import java.util.Collection;


/**
 *
 */
public interface ClusterManager {
    Collection<Instance> getClusterInstances( Cluster cluster );

    void createCluster( Cluster cluster );


}
