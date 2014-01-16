package org.safehaus.chop.api;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


/** The S3 Service is used to register the node so other nodes in the same perftest formation can access it. */
public interface StoreService {

    /**
     * Starts up this StoreService.
     */
    void start();

    /**
     * Checks if this StoreService has started.
     *
     * @return true if started, false otherwise
     */
    boolean isStarted();

    /**
     * Stops this StoreService.
     */
    void stop();

    /**
     * Gets the runner instance information from the store as a map of their keys to
     * their RunnerFig information.
     *
     * @return the keys mapped to RunnerFig information
     */
    Map<String, RunnerFig> getRunners();

    /**
     * Gets the runnerFig instance information from the store as a map of keys RunnerFig instances.
     *
     * @param runnerFig a runnerFig to exclude from results (none if null)
     *
     * @return the keys mapped to RunnerFig instance
     */
    Map<String, RunnerFig> getRunners( RunnerFig runnerFig );

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
    void store( Project project, ISummary summary, File resultsFile, Class<?> testClass );

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
    void register( RunnerFig runner );

    /**
     * Removes this Runner's registration.
     *
     * @param runner the runners information
     */
    void unregister( RunnerFig runner );

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
    boolean hasCompleted( RunnerFig runner, Project project, int runNumber, Class<?> testClass );

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
    int getNextRunNumber( RunnerFig runner, Project project );
}
