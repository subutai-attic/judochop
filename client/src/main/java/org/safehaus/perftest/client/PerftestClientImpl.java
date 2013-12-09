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


    @Override
    public Result stop( final RunnerInfo runner, final boolean propagate ) {
        return new BaseResult( "http://localhost:8080", true, "test stopped", State.STOPPED );
    }


    @Override
    public Result reset( final RunnerInfo runner, final boolean propagate ) {
        return new BaseResult( "http://localhost:8080", true, "test reset", State.READY );
    }


    @Override
    public Result scan( final RunnerInfo runner, final boolean propagate ) {
        return new BaseResult( "http://localhost:8080", true, "scan triggered", State.READY );
    }


    @Override
    public Result verify() {
        return new BaseResult( "http://localhost:8080", true, "verification triggered", State.READY );
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
