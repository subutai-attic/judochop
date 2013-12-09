package org.safehaus.perftest.client;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.TestInfo;


/**
 * A client to interact with the perftest system and it's test results archive.
 *
 * <ul>
 *     <li>listing registered runners in the cluster</li>
 *     <li>listing and deleting uploaded test jars and their test information</li>
 *     <li>downloading and collating test run results from runners</li>
 * </ul>
 */
public interface PerftestClient extends ConfigKeys {

    /**
     * Gets the set of runners involved in a perftest cluster formation.
     *
     * @return the set of Perftest runner nodes
     */
    Collection<RunnerInfo> getRunners();


    /**
     * Gets the set of tests that have been created.
     *
     * @return the set of performance tests
     */
    Set<TestInfo> getTests() throws IOException;


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
    File getResults( RunInfo run ) throws IOException;


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
     * Loads a new test to be run by the perftest cluster formation. When called
     * will propagation enabled, all peers in the cluster should load the new
     * test. The call will automatically handle verification to make sure the
     * cluster formation is consistent and each node is in the State.READY state
     * to start running tests. It will block until the verification is found to
     * fail or until the cluster is consistent.
     *
     * @param runner the runner to use for propagating the load request
     * @param testKey the test information associated with the test to load
     * @param propagate whether or not to make the call propagate
     * @return the results associated with the operation
     */
    Result load( RunnerInfo runner, String testKey, Boolean propagate );


    /**
     *
     * @param runner
     * @param propagate
     * @return
     */
    Result stop( RunnerInfo runner, boolean propagate );


    Result status( RunnerInfo runner );


    Result reset( RunnerInfo runner, boolean propagate );


    Result scan( RunnerInfo runner, boolean propagate );


    /**
     * Verifies perftest cluster formation consistency. This means that:
     *
     * <ul>
     *     <li>all registered peers are alive in the cluster</li>
     *     <li>all registered peers are consistent:
     *       <ul>
     *           <li>have the same git version UUID</li>
     *           <li>have the same load timestamp</li>
     *           <li>have the same md5 checksum</li>
     *       </ul>
     *     </li>
     *     <li>all registered peers are in the READY state to start the test</li>
     * </ul>
     *
     * @return the results of the verification
     */
    Result verify ();


    /**
     * Gets the first available live runner.
     *
     * @return the first available live runner
     */
    RunnerInfo getLiveRunner();
}
