package org.apache.usergrid.chop.spi;


import org.apache.usergrid.chop.stack.Instance;
import org.apache.usergrid.chop.stack.InstanceSpec;


/**
 * Results of launching an instance.
 */
public interface LaunchResult {
    int getCount();

    InstanceSpec getInstanceSpec();

    Iterable<Instance> getInstances();
}
