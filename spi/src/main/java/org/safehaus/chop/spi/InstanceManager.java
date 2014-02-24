package org.safehaus.chop.spi;


import java.util.Collection;


/**
 * Manages instances.
 */
public interface InstanceManager {
    Provider getProvider();

    int getDefaultTimeout();

    String terminateInstances( Collection<String> instancesIds );

    LaunchResult launchInstances( InstanceSpec spec, int count, int timeout );

    Collection<Instance> getInstances();

    Collection<Instance> getInstances( InstanceState state );

    Collection<Instance> getInstances( String instanceName );
}
