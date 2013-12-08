package org.safehaus.perftest.client;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.TestInfo;
import org.safehaus.perftest.api.store.StoreOperations;
import org.safehaus.perftest.client.rest.LoadRequest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.config.DynamicStringProperty;


/**
 * An implementation of the PerftestClient interface.
 */
@Singleton
public class PerftestClientImpl implements PerftestClient, org.safehaus.perftest.api.store.amazon.ConfigKeys {
    private final StoreOperations operations;
    @Inject @Named( AWS_BUCKET_KEY ) private DynamicStringProperty awsBucket;


    @Inject
    public PerftestClientImpl( StoreOperations operations )
    {
        this.operations = operations;
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
    public Result load( RunnerInfo runner, String testKey ) {
        return new LoadRequest().load( runner, testKey );
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
}
