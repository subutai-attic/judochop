package org.safehaus.chop.spi;


import java.util.Collection;

import org.safehaus.chop.stack.Cluster;
import org.safehaus.chop.stack.Instance;


/**
 *
 */
public interface ClusterManager {
    Collection<Instance> getClusterInstances( Cluster cluster );

    void createCluster( Cluster cluster );


}
