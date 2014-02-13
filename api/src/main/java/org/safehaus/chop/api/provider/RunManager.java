package org.safehaus.chop.api.provider;


import java.io.File;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.Summary;


/**
 * Manages run information.
 */
public interface RunManager {
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
     * @param project the project configuration
     * @return the next available run number
     */
    int getNextRunNumber( Project project );

}