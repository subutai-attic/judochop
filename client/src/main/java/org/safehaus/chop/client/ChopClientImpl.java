package org.safehaus.chop.client;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.StoreService;
import org.safehaus.chop.client.rest.RestRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * An implementation of the ChopClient interface.
 */
@Singleton
public class ChopClientImpl implements ChopClient, Constants {
    private static final Logger LOG = LoggerFactory.getLogger( ChopClientImpl.class );
    private final StoreService service;


    @Inject
    public ChopClientImpl( StoreService service )
    {
        this.service = service;
    }


    @Override
    public void deleteTests() {
        service.deleteProjects();
    }


    @Override
    public Collection<Runner> getRunners() {
        return service.getRunners().values();
    }


    @Override
    public Set<Project> getProjectConfigs() throws IOException {
        return service.getProjects();
    }


    @Override
    public Set<ISummary> getRuns( final Project test ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public File getResults( final ISummary run ) throws IOException {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public void delete( final ISummary run ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public void delete( final Project test ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public Result load( Runner runner, String projectKey, Map<String,String> props ) {
        Project project = getProject( projectKey );
        String md5 = project.getWarMd5();

        LOG.info( "Sending load request to " + runner.getHostname() );

        Result result = RestRequests.load( runner, projectKey, props );

        if ( ! result.getStatus() ) {
            LOG.info( "Failed on load POST to {} response message was: {}", result.getEndpoint(), result.getMessage() );
            return result;
        }

        Collection<Runner> runners;
        runners = new ArrayList<Runner>( 1 );
        runners.add( runner );
        LinkedList<Runner> failed = new LinkedList<Runner>( runners );

        // Wait a little for the drivers to come back up
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

            Runner runnerInfo = failed.removeLast();

            try {
                Result status = status( runnerInfo );
                Project remoteInfo = status.getProject();

                if ( ! status.getStatus() ) {
                    LOG.info( "Runner {} failed on status call", runnerInfo );
                    failed.addFirst( runnerInfo );
                }
                else if ( status.getState() != State.READY ) {
                    LOG.info( "Runner {} not yet in READY state", runnerInfo );
                    failed.addFirst( runnerInfo );
                }
                else if ( remoteInfo == null || remoteInfo.getWarMd5() == null ) {
                    failed.addFirst( runnerInfo );
                }
                else if ( ! remoteInfo.getWarMd5().equals( md5 ) ) {
                    throw new RuntimeException( "Runner at " + runnerInfo.getHostname() +
                            " has wrong md5, was expecting " + md5 );
                }
                else {
                    LOG.info( "Runner {} is backup and READY!", runnerInfo );
                }
            }
            catch ( Exception e ) {
                LOG.debug( "Runner {} failed on status call", runnerInfo, e );
                failed.addFirst( runnerInfo );
            }
        }

        return result;
    }


    private Project getProject( final String testKey ) {
        return service.getProject( testKey );
    }


    @Override
    public Result status( Runner runner ) {
        return RestRequests.status( runner );
    }


    /**
     * Sends the start rest request to runner.
     *
     * @param runner the runner configuration
     * @return the result
     */
    @Override
    public Result start( Runner runner ) {
        return RestRequests.start( runner );
    }


    /**
     * Checks if the given runner is in RUNNING state. If that check succeeds, sends the stop rest request
     * @param runner the runner configuration
     * @return the result
     */
    @Override
    public Result stop( final Runner runner ) {
        Result status;
        boolean stoppable = false;

        status = status( runner );
        if ( status.getStatus() && status.getState() == State.RUNNING ) {
            stoppable = true;
        }

        if ( ! stoppable ) {
            LOG.info( "Runner is not in a stoppable state" );
            return new BaseResult( status.getEndpoint(), false, "Cannot stop", status.getState() );
        }

        LOG.info( "Sending stop request to runner at {}", runner.getHostname() );
        return RestRequests.stop( runner );
    }


    /**
     * Checks if the given runner is in STOPPED state. If that check succeeds, sends the reset
     * rest request.
     *
     * @param runner the runner configuration
     * @return the result
     */
    @Override
    public Result reset( final Runner runner ) {
        Result status = status( runner );
        boolean resettable = ( status.getStatus() && status.getState() == State.STOPPED );

        if ( ! resettable ) {
            LOG.info( "Runner is not in a resettable state" );
            return new BaseResult( status.getEndpoint(), false, "Cannot reset", status.getState() );
        }

        LOG.info( "Sending reset request to runner at {}", runner.getHostname() );
        return RestRequests.reset( runner );
    }


    /**
     * Checks whether all the drivers in the cluster are reachable, in a READY state
     * and has the latest test with matching MD5 checksum fields.
     * @return Returns true if all instances on the cluster is ready to start the test
     */
    @Override
    public Result verify() {
        LOG.info( "Starting verify operation..." );

        String message;

        // Get the latest project information from store
        Project project = null;
        try {
            Set<Project> projects = getProjectConfigs();

            for ( Project projectCandidate : projects ) {
                if ( project == null ) {
                    project = projectCandidate;
                }
                else if ( compareTimestamps(
                        projectCandidate.getCreateTimestamp(), project.getCreateTimestamp() ) > 0 ) {
                    project = projectCandidate;
                }
            }
        } catch ( Exception e ) {
            message = "Error while getting project information from store";
            LOG.warn( message, e );
            return new BaseResult( null, false, message, State.INACTIVE );
        }

        if ( project == null ) {
            return new BaseResult( null, false, "No projects were found in the store", State.INACTIVE );
        }

        LOG.info( "Got the latest project info from store" );
        LOG.info( "Latest project MD5: {}, Create Time: {}", project.getWarMd5(), project.getCreateTimestamp() );

        Collection<Runner> runners = getRunners();
        for ( Runner runner : runners ) {
            try {
                LOG.info( "Verifying runner with hostname {}", runner.getHostname() );
                Result result = status( runner );

                if ( ! result.getStatus() ) {
                    LOG.info( "State of runner could not be retrieved" );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    return result;
                }
                if ( ! result.getState().accepts( Signal.START, State.RUNNING ) ) {
                    LOG.info( "Runner is not in a ready state, State: {}", result.getState() );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    return result;
                }
                if ( ! result.getProject().getWarMd5().equals( project.getWarMd5() ) ) {
                    message = "Runner doesn't have the latest test loaded";
                    LOG.info( message );
                    LOG.info( "Runner hostname: {}", runner.getHostname() );
                    LOG.info( "Latest test MD5 is {}", project.getWarMd5() );
                    LOG.info( "Runner's installed MD5 is {}", result.getProject().getWarMd5() );
                    return new BaseResult( result.getEndpoint(), false, message, result.getState() );
                }
                LOG.info( "Runner is READY: {}", runner );
            } catch ( Exception e ) {
                message = "Error while getting runner states";
                LOG.warn( message, e );
                return new BaseResult( runner.getHostname(), false, message, State.INACTIVE );
            }
        }

        message = "Cluster is ready to start...";
        LOG.info( message );

        return new BaseResult( null, true, message, State.READY );
    }


    /**
     * Compares to timestamps and returns -1 if ts1 < ts2, 0 if ts1 == ts2, 1 otherwise
     * @param ts1 format is 'yyyy.MM.dd.HH.mm.ss'
     * @param ts2 format is 'yyyy.MM.dd.HH.mm.ss'
     * @return returns -1 if ts1 < ts2, 0 if ts1 == ts2, 1 otherwise
     * @throws NumberFormatException Invalid formatting in given timestamps
     */
     public int compareTimestamps ( String ts1, String ts2 ) throws NumberFormatException {

        String[] rawFields1 = ts1.split( "\\." );
        String[] rawFields2 = ts2.split( "\\." );

        if ( rawFields1.length != rawFields2.length ) {
            throw new NumberFormatException( "Timestamp format is wrong" );
        }

        int field1, field2;
        for ( int i = 0; i < rawFields1.length; i++ ) {
            field1 = Integer.parseInt( rawFields1[i] );
            field2 = Integer.parseInt( rawFields2[i] );

            if ( field1 != field2 ) {
                return Integer.signum( field1 - field2 );
            }
        }

        return 0;
    }


    @Override
    public Runner getLiveRunner() {
        Collection<Runner> candidates = getRunners();

        for ( Runner runner : candidates )
        {
            try {
                Result result = status( runner );

                if ( result.getStatus() && result.getState().accepts( Signal.LOAD, State.READY ) )
                {
                    return runner;
                }
            }
            catch ( Exception e ) {
                LOG.warn( "Runner {} does not seem to be responding.", runner );
            }
        }

        LOG.error( "Could not find a live runner: blowing chunks!" );
        throw new RuntimeException( "No live drivers available" );
    }
}
