package org.safehaus.chop.spi;


import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Runner;


/**
 * A registry service for Runners.
 */
public interface RunnerRegistry {

    /**
     * Starts up this RunnerRegistry.
     */
    void start();

    /**
     * Checks if this RunnerRegistry has started.
     *
     * @return true if started, false otherwise
     */
    boolean isStarted();

    /**
     * Stops this RunnerRegistry.
     */
    void stop();

    /**
     * Gets the runner instance information from the RunnerRegistry as a map of their keys to
     * their Runner information.
     *
     * @return the keys mapped to Runner information
     */
    Map<String, Runner> getRunners();

    /**
     * Gets the runner instance information from the RunnerRegistry as a map of keys Runner instances.
     *
     * @param runner a runner to exclude from results (none if null)
     *
     * @return the keys mapped to Runner instance
     */
    Map<String, Runner> getRunners( Runner runner );

    /**
     * Registers this runner instance by adding its instance information into the
     * RunnerRegistry as a properties file using the following key format:
     *
     * "$RUNNERS_PATH/publicHostname.properties"
     *
     * @param runner the runner's configuration instance to be registered
     */
    void register( Runner runner );

    /**
     * Removes this Runner's registration.
     *
     * @param runner the runners information
     */
    void unregister( Runner runner );

    /**
     * Deletes all the runner registrations outside of the provided collection.
     *
     * @param activeRunners the hostnames of the active runners that should NOT be deleted
     */
    void deleteGhostRunners( Set<String> activeRunners );
}

