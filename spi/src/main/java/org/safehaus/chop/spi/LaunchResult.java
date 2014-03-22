package org.safehaus.chop.spi;


import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.stack.InstanceSpec;


/**
 * Results of launching an instance.
 */
public interface LaunchResult {
    int getCount();

    InstanceSpec getInstanceSpec();

    Iterable<Instance> getInstances();
}
