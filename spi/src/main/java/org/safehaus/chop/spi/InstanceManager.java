package org.safehaus.chop.spi;


import java.util.Collection;

import org.safehaus.chop.stack.ICoordinatedCluster;
import org.safehaus.chop.stack.ICoordinatedStack;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.stack.InstanceSpec;
import org.safehaus.chop.stack.InstanceState;


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
