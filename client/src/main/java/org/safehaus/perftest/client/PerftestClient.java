package org.safehaus.perftest.client;


import java.io.File;
import java.io.IOException;
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
public interface PerftestClient extends ConfigKeys {
    String FORMATION_KEY = "perftest.formation";

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
     * Loads a new test to be run by the perftest cluster formation. This call will
     * automatically propagated to all peers in the cluster and will automatically
     * handle verification to make sure the cluster formation is consistent and
     * in the State.READY state to start running tests. It will block until the
     * verification is found to fail or until the cluster is consistent.
     *
     * @param test the test information associated with the test to load
     * @return the results associated with the operation
     */
    Result load( TestInfo test );


    /**
     *
     * @param runner
     * @param propagate
     * @return
     */
    Result stop( RunnerInfo runner, boolean propagate );


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
     * @param formation the formation to verify the consistency of
     * @return the results of the verification
     */
    Result verify ( String formation );
}
