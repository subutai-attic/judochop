package org.apache.usergrid.chop.spi;


import java.util.Collection;

import org.apache.usergrid.chop.stack.Cluster;
import org.apache.usergrid.chop.stack.Instance;


/**
 *
 */
public interface ClusterManager {
    Collection<Instance> getClusterInstances( Cluster cluster );

    void createCluster( Cluster cluster );


}
