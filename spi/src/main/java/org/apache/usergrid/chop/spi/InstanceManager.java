package org.apache.usergrid.chop.spi;


import java.util.Collection;

import org.apache.usergrid.chop.stack.ICoordinatedCluster;
import org.apache.usergrid.chop.stack.ICoordinatedStack;
import org.apache.usergrid.chop.stack.Instance;
import org.apache.usergrid.chop.stack.InstanceSpec;


/**
 * Manages instances.
 */
public interface InstanceManager {
    int getDefaultTimeout();

    void terminateInstances( Collection<String> instancesIds );

    LaunchResult launchCluster( ICoordinatedStack stack, ICoordinatedCluster cluster, int timeout );

    LaunchResult launchRunners( ICoordinatedStack stack, InstanceSpec spec, int count, int timeout );

    /** Returns all cluster instances defined by stack and cluster */
    Collection<Instance> getClusterInstances( ICoordinatedStack stack, ICoordinatedCluster cluster );

    /** Returns all runner instances defined by stack */
    Collection<Instance> getRunnerInstances( ICoordinatedStack stack );
}
