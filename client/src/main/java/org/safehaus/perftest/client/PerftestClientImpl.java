package org.safehaus.perftest.client;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.Signal;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.TestInfo;
import org.safehaus.perftest.api.store.StoreOperations;
import org.safehaus.perftest.client.rest.RestRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.config.DynamicStringProperty;


/**
 * An implementation of the PerftestClient interface.
 */
@Singleton
public class PerftestClientImpl implements PerftestClient, org.safehaus.perftest.api.store.amazon.ConfigKeys {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientImpl.class );
    private final StoreOperations operations;
    @Inject @Named( AWS_BUCKET_KEY ) private DynamicStringProperty awsBucket;


    @Inject
    public PerftestClientImpl( StoreOperations operations )
    {
        this.operations = operations;
    }


    @Override
    public void deleteTests() {
        operations.deleteTests();
    }


    @Override
    public Collection<RunnerInfo> getRunners() {
        return operations.getRunners().values();
    }


    @Override
    public Set<TestInfo> getTests() throws IOException {
        return operations.getTests();
    }


    @Override
    public Set<RunInfo> getRuns( final TestInfo test ) {
        return null;
    }


    @Override
    public File getResults( final RunInfo run ) throws IOException {
        return File.createTempFile( "foo", "bar" );
    }


    @Override
    public void delete( final RunInfo run ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public void delete( final TestInfo test ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public Result load( RunnerInfo runner, String testKey, Boolean propagate ) {
        TestInfo testInfo = getTest( testKey );
        String md5 = testInfo.getWarMd5();

        Result result = RestRequests.load( runner, testKey, propagate );

        if ( ! result.getStatus() )
        {
            return result;
        }

        Collection<RunnerInfo> runners = getRunners();
        LinkedList<RunnerInfo> failed = new LinkedList<RunnerInfo>( runners );

        // Wait a little for the runners to come back up
        try {
            Thread.sleep( 5000L );
        }
        catch ( InterruptedException e ) {
            LOG.warn( "Dang someone just woke me up before it was time!", e );
        }

        long startTime = System.currentTimeMillis();

        // Start going through them in linked list remove from back add to front if not up
        while ( ! failed.isEmpty() ) {
            if ( System.currentTimeMillis() - startTime > 30000L ) {
                LOG.error( "Some Runners cannot seem to come up. Abandoning retry attempts." );
                return new BaseResult( runner.getUrl() + "/load", false,
                        "failed to verify all nodes as ready", State.INACTIVE );
            }

            RunnerInfo runnerInfo = failed.removeLast();

            try {
                Result status = status( runnerInfo );
                TestInfo remoteInfo = status.getTestInfo();

                if ( ! status.getStatus() ) {
                    LOG.warn( "Runner {} failed on status call", runnerInfo );
                    failed.addFirst( runnerInfo );
                }

                if ( status.getState() != State.READY ) {
                    LOG.warn( "Runner {} not yet in READY state", runnerInfo );
                    failed.addFirst( runnerInfo );
                }

                if ( remoteInfo != null && remoteInfo.getWarMd5() != null ) {
                    if ( ! remoteInfo.getWarMd5().equals( md5 ) ) {
                        LOG.warn( "Runner {} has wrong md5 ... was expecting {}", runnerInfo, md5 );
                        failed.addFirst( runnerInfo );
                    }
                }

                LOG.info( "Runner {} is backup and READY!", runnerInfo );
            }
            catch ( Exception e ) {
                LOG.warn( "Runner {} failed on status call", runnerInfo, e );
                failed.addFirst( runnerInfo );
            }
        }

        return result;
    }


    private TestInfo getTest( final String testKey ) {
        return operations.getTestInfo( testKey );
    }


    @Override
    public Result status( RunnerInfo runner ) {
        return RestRequests.status( runner );
    }


    /**
     * Sends the start rest request to runner with given propagate value.
     * Call verify() method first to make sure all runners are ready and up-to-date, call status() if you're not
     * going to propagate.
     * @param runner
     * @param propagate
     * @return
     */
    @Override
    public Result start( RunnerInfo runner, final boolean propagate ) {
        return RestRequests.start( runner, propagate );
    }


    /**
     * If propagate is true, checks if there is at least one runner in cluster in RUNNING state; if not, checks
     * if the given runner is in RUNNING state. If that check succeeds, sends the stop rest request
     * @param runner
     * @param propagate
     * @return
     */
    @Override
    public Result stop( final RunnerInfo runner, final boolean propagate ) {
        Result status = null;
        boolean stoppable = false;
        // if request is wished to be propagated, then we check if there is at least one runner in RUNNING state
        if ( propagate ) {
            Collection<RunnerInfo> runners = getRunners();
            for ( RunnerInfo r : runners ) {
                status = status( r );
                if ( status.getStatus() && status.getState() == State.RUNNING ) {
                    stoppable = true;
                    break;
                }
            }
        }
        else {
            status = status( runner );
            if ( status.getStatus() && status.getState() == State.RUNNING ) {
                stoppable = true;
            }
        }

        if ( ! stoppable ) {
            LOG.info( "Cluster is not in a stoppable state" );
            return new BaseResult( status.getEndpoint(), true, "Cannot stop", status.getState() );
        }

        LOG.info( "Sending stop request to runner at {}", runner.getHostname() );
        return RestRequests.stop( runner, propagate );
    }


    /**
     * If propagate is true, checks if there is at least one runner in cluster in STOPPED state; if not, checks
     * if the given runner is in STOPPED state. If that check succeeds, sends the reset rest request
     * @param runner
     * @param propagate
     * @return
     */
    @Override
    public Result reset( final RunnerInfo runner, final boolean propagate ) {
        Result status = status( runner );
        boolean resettable = false;
        // if request is wished to be propagated, then we check if there is at least one runner in STOPPED state
        if ( propagate ) {
            Collection<RunnerInfo> runners = getRunners();
            for ( RunnerInfo r : runners ) {
                status = status( r );
                if ( status.getStatus() && status.getState() == State.STOPPED ) {
                    resettable = true;
                    break;
                }
            }
        }
        else {
            resettable = ( status.getStatus() && status.getState() == State.STOPPED );
        }

        if ( ! resettable ) {
            LOG.info( "Cluster is not in a resettable state" );
            return new BaseResult( status.getEndpoint(), true, "Cannot reset", status.getState() );
        }

        LOG.info( "Sending reset request to runner at {}", runner.getHostname() );
        return RestRequests.reset( runner, propagate );
    }


    @Override
    public Result scan( final RunnerInfo runner, final boolean propagate ) {
        return new BaseResult( "http://localhost:8080", true, "scan triggered", State.READY );
    }


    /**
     * Checks whether all the runners in the cluster are reachable, in a READY state
     * and has the latest test with matching MD5 checksum fields.
     * @return Returns true if all instances on the cluster is ready to start the test
     */
    @Override
    public boolean verify() {
        // Get the latest test info
        TestInfo latestTest = null;
        try {
            Set<TestInfo> tests = getTests();

            for ( TestInfo test : tests ) {
                if ( latestTest == null ) {
                    latestTest = test;
                }
                else if ( compareTimestamps( test.getCreateTimestamp(), latestTest.getCreateTimestamp() ) > 0 ) {
                    latestTest = test;
                }
            }

        } catch ( Exception e ) {
            LOG.warn( "Error while getting test information from store", e );
            return false;
        }

        if ( latestTest == null ) {
            LOG.info( "No tests found on store" );
            return false;
        }

        Collection<RunnerInfo> runners = getRunners();
        for ( RunnerInfo runner : runners ) {
            try {
                Result result = status( runner );
                if ( ! result.getStatus() ) {
                    LOG.info( "State of runner could not be retrieved" );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    return false;
                }
                if ( result.getState().next( Signal.LOAD ) != State.READY )
                {
                    LOG.info( "Runner is not in a ready state, State: {}", result.getState() );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    return false;
                }
                if ( ! result.getTestInfo().getWarMd5().equals( latestTest.getWarMd5() ) ) {
                    LOG.info( "Runner doesn't have the latest test loaded" );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    LOG.info( "Latest test MD5 is {}", latestTest.getWarMd5() );
                    LOG.info( "Runner's installed MD5 is {}", result.getTestInfo().getWarMd5() );
                    return false;
                }
                LOG.info( "Runner is READY: {}", runner );
            } catch ( Exception e ) {
                LOG.warn( "Error while getting runner states", e );
                return false;
            }
        }
        return true;
    }


    /**
     * Compares to timestamps and returns -1 if ts1 < ts2, 0 if ts1 == ts2, 1 otherwise
     * @param ts1 format is 'yyyy.MM.dd.hh.mm.ss'
     * @param ts2 format is 'yyyy.MM.dd.hh.mm.ss'
     * @return returns -1 if ts1 < ts2, 0 if ts1 == ts2, 1 otherwise
     * @throws NumberFormatException Invalid formatting in given timestamps
     */
    private int compareTimestamps ( String ts1, String ts2 ) throws NumberFormatException {

        String[] rawFields1 = ts1.split( "." );
        String[] rawFields2 = ts2.split( "." );

        if ( rawFields1.length != rawFields2.length ) {
            throw new NumberFormatException( "Timestamp format is wrong" );
        }

        int field1, field2;
        for ( int i = 0; i < rawFields1.length; i++ ) {
            field1 = Integer.parseInt( rawFields1[i] );
            field2 = Integer.parseInt( rawFields2[i] );

            if ( field1 != field2 ) {
                return Integer.signum( field1 - field1 );
            }
        }

        return 0;
    }


    @Override
    public RunnerInfo getLiveRunner() {
        Collection<RunnerInfo> candidates = getRunners();

        for ( RunnerInfo runner : candidates )
        {
            try {
                Result result = status( runner );

                if ( result.getStatus() && result.getState().next( Signal.LOAD ) == State.READY )
                {
                    return runner;
                }
            }
            catch ( Exception e ) {
                LOG.warn( "Runner {} does not seem to be responding.", runner );
            }
        }

        LOG.error( "Could not find a live runner: blowing chunks!" );
        throw new RuntimeException( "No live runners available" );
    }
}
