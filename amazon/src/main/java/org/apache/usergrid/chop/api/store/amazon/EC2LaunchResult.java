package org.apache.usergrid.chop.api.store.amazon;


import java.util.Collection;

import org.safehaus.chop.spi.LaunchResult;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.stack.InstanceSpec;


public class EC2LaunchResult implements LaunchResult {

    private InstanceSpec instanceSpec;
    private Collection<Instance> instances;


    public EC2LaunchResult( InstanceSpec spec, Collection<Instance> instances ) {
        this.instanceSpec = spec;
        this.instances = instances;
    }

    @Override
    public int getCount() {
        return instances.size();
    }


    @Override
    public InstanceSpec getInstanceSpec() {
        return instanceSpec;
    }


    @Override
    public Iterable<Instance> getInstances() {
        return instances;
    }
}
