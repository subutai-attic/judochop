package org.safehaus.chop.api;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


/** The Store is used to register the node so other nodes in the same perftest formation can access it. */
public interface Store {

    /**
     * Starts up this Store.
     */
    void start();

    /**
     * Checks if this Store has started.
     *
     * @return true if started, false otherwise
     */
    boolean isStarted();

    /**
     * Stops this Store.
     */
    void stop();

    /**
     * Gets the runner instance information from the store as a map of their keys to
     * their Runner information.
     *
     * @return the keys mapped to Runner information
     */
    Map<String, Runner> getRunners();

    /**
     * Gets the runner instance information from the store as a map of keys Runner instances.
     *
     * @param runner a runner to exclude from results (none if null)
     *
     * @return the keys mapped to Runner instance
     */
    Map<String, Runner> getRunners( Runner runner );

    /**
     * Downloads a file from the store by key, and places it in a temporary file returning
     * the file. Use this to download big things like war files or results.
     *
     * @param tempDir the temporary directory to use
     * @param runnerWar the path
     * @return the File object referencing the temporary file
     *
     * @throws IOException if there's a problem accessing the stream
     */
    File download( File tempDir, String runnerWar ) throws Exception;

    /**
     * Stores the summary and results file for a chop test run into the store.
     *
     * @param project the project associated with the run
     * @param summary the summary information associated with the test run
     * @param resultsFile the results log file
     * @param testClass the chopped test class
     */
    void store( Project project, Summary summary, File resultsFile, Class<?> testClass );

    /**
     * Stores the project test information.
     *
     * @param project the Project object to be serialized and stored
     */
    void store( Project project );

    /**
     * Tries to load a Project file based on prepackaged runner metadata: the runner's
     * loadKey. If it cannot find it, null is returned.
     *
     * @param runnerWar the load key for the runner war
     * @return the Project object if it exists in the store or null if it does not
     */
    Project getProject( String runnerWar );

    /**
     * Registers this runner instance by adding its instance information into the
     * store as a properties file using the following key format:
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
     * Scans for projects with test information under the bucket as:
     * </p>
     * "$CONFIGS_PATH/.*\/$PROJECT_FILE
     *
     * @return a set of keys as Strings for test information
     */
    Set<Project> getProjects() throws IOException;

    /**
     * Deletes all the projects in the store.
     */
    void deleteProjects();

    /**
     * Deletes all the runner registrations outside of the provided collection.
     *
     * @param activeRunners the hostnames of the active runners that should NOT be deleted
     */
    void deleteGhostRunners( Set<String> activeRunners );

    /**
     * Checks to see if a runner has deposited run summary information for a chopped test in
     * the store.
     *
     * @param runner the runner to check for chop completion
     * @param project the project being run
     * @param runNumber the run number
     * @param testClass the chopped test to check for completion on
     * @return true if the summary information has been deposited, false otherwise
     */
    boolean hasCompleted( Runner runner, Project project, int runNumber, Class<?> testClass );

    /**
     * Checks the store to find the next available run number starting at 1. This method
     * needs to be used with extreme caution. It should only be used when starting up a
     * runner's controller. The intention is to be able to enable Judo Chop to restart
     * runner containers between runs to refresh the Tomcat container.
     *
     * WARNING: It should not be used at any time other than runner initialization since if
     * used with many runners concurrently during test dumps to the store, it could result
     * in race conditions. During runner initialization this is not a possibility on the
     * same project.
     *
     * @param runner the runner configuration
     * @param project the project configuration
     * @return the next available run number
     */
    int getNextRunNumber( Runner runner, Project project );
}
