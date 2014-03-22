package org.safehaus.chop.spi;


import java.util.Collection;

import org.safehaus.chop.stack.InstanceSpec;


/**
 * Manages instances.
 */
public interface InstanceManager {
    int getDefaultTimeout();

    String terminateInstances( Collection<String> instancesIds );

    LaunchResult launchInstances( InstanceSpec spec, int count, int timeout );

    Collection<Instance> getInstances();

    Collection<Instance> getInstances( InstanceState state );

    Collection<Instance> getInstances( String instanceName );
}
