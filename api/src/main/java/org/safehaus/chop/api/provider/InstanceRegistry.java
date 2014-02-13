package org.safehaus.chop.api.provider;


import java.util.Collection;
import java.util.Set;


/**
 * A registry service for Runners.
 */
public interface InstanceRegistry {

    /**
     * Starts up this Registry.
     */
    void start();

    /**
     * Checks if this Registry has started.
     *
     * @return true if started, false otherwise
     */
    boolean isStarted();

    /**
     * Stops this Registry.
     */
    void stop();

    Collection<Instance> getInstances();

    void register( Instance instance );

    void unregister( Instance instance );

    void deleteGhostInstances( Set<String> exclusions );
}

