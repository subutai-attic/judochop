package org.apache.usergrid.chop.spi;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.usergrid.chop.api.Runner;

/**
 * A registry service for Runners.
 */
public interface RunnerRegistry {
    /**
     * Gets the runner instance information from the RunnerRegistry as a map of their keys to
     * their Runner information.
     *
     * @return the keys mapped to Runner information
     */
    List<Runner> getRunners();

    /**
     * Gets the runner instance information from the RunnerRegistry as a map of keys Runner instances.
     *
     *
     * @param runner a runner to exclude from results (none if null)
     *
     * @return the keys mapped to Runner instance
     */
    List<Runner> getRunners( Runner runner );

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
}

