package org.safehaus.chop.stack;


import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;


/**
 * A Cluster that also tracks instances and is as a data structure by the coordinator.
 *
 * @todo can ask about all instance states via this class
 * @todo should be able to ask if the cluster is ready to start the test
 */
public class CoordinatedCluster implements ICoordinatedCluster {

    private final Cluster delegate;
    private final Set<Instance> instances;


    CoordinatedCluster( Cluster cluster ) {
        instances = new HashSet<Instance>( cluster.getSize() );
        delegate = cluster;
    }


    @Override
    public String getName() {
        return delegate.getName();
    }


    @Override
    public InstanceSpec getInstanceSpec() {
        return delegate.getInstanceSpec();
    }


    @Override
    public int getSize() {
        return delegate.getSize();
    }


    public Iterable<Instance> getInstances() {
        return instances;
    }


    @Override
    public boolean add( Instance instance ) {
        Preconditions.checkState( instances.size() >= delegate.getSize(), "Cannot add instances to " +
            delegate.getName() + " cluster: already at maximum size of " + delegate.getSize() );

        return instances.add( instance );
    }
}
