package org.safehaus.chop.spi;


import java.util.Collection;

import org.safehaus.chop.stack.Cluster;


/**
 *
 */
public interface ClusterManager {
    Collection<Instance> getClusterInstances( Cluster cluster );

    void createCluster( Cluster cluster );


}
