package org.safehaus.perftest.client;


import java.io.File;
import java.util.Set;

import org.safehaus.perftest.Result;
import org.safehaus.perftest.RunInfo;
import org.safehaus.perftest.RunnerInfo;
import org.safehaus.perftest.TestInfo;


/**
 * A client to interact with the perftest system and it's test results archive.
 *
 * <ul>
 *     <li>listing registered runners in the cluster</li>
 *     <li>listing and deleting uploaded test jars and their test information</li>
 *     <li>downloading and collating test run results from runners</li>
 * </ul>
 */
public interface PerftestClient {
    /**
     * Gets the set of runners involved in a perftest cluster formation.
     *
     * @param formation the perftest cluster formation
     * @return the set of Perftest runner nodes
     */
    Set<RunnerInfo> getRunners( String formation );


    /**
     * Gets the set of tests that have been created.
     *
     * @param formation the perftest cluster formation
     * @return the set of performance tests
     */
    Set<TestInfo> getTests( String formation );


    /**
     * Gets the set of runs associated with a test.
     *
     * @return the set of runs that have taken place on a test.
     */
    Set<RunInfo> getRuns( TestInfo test );


    /**
     * Pulls down results of a test run and returns a File to a collated version of them.
     *
     * @param run the run to use to get the results from
     * @return a collated version of the results from each runner
     */
    File getResults( RunInfo run );


    /**
     * Deletes the results and run information associated with a test run.
     *
     * @param run the run information and its results to delete.
     */
    void delete( RunInfo run );


    /**
     * Deletes the test executables, its information, and all run results.
     *
     * @param test the test to
     */
    void delete( TestInfo test );


    /**
     *
     *
     * @param test
     * @param propagate
     * @return
     */
    Result load( TestInfo test, boolean propagate );


    Result stop( RunnerInfo runner, boolean propagate );


    Result reset( RunnerInfo runner, boolean propagate );


    Result scan( RunnerInfo runner, boolean propagate );
}
