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
import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.StoreService;
import org.safehaus.chop.client.rest.RestRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * An implementation of the PerftestClient interface.
 */
@Singleton
public class PerftestClientImpl implements PerftestClient, Constants {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientImpl.class );
    private final StoreService service;


    @Inject
    public PerftestClientImpl( StoreService service )
    {
        this.service = service;
    }


    @Override
    public void deleteTests() {
        service.deleteProjects();
    }


    @Override
    public Collection<RunnerFig> getRunners() {
        return service.getRunners().values();
    }


    @Override
    public Set<ProjectFig> getProjectConfigs() throws IOException {
        return service.getProjects();
    }


    @Override
    public Set<ISummary> getRuns( final ProjectFig test ) {
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
    public void delete( final ProjectFig test ) {
        throw new RuntimeException( "Not implemented yet" );
    }


    @Override
    public Result load( RunnerFig runnerFig, String projectKey, Map<String,String> props ) {
        ProjectFig project = getProject( projectKey );
        String md5 = project.getWarMd5();

        LOG.info( "Sending load request to " + runnerFig.getHostname() );

        Result result = RestRequests.load( runnerFig, projectKey, props );

        if ( ! result.getStatus() ) {
            LOG.info( "Failed on load POST to {} response message was: {}", result.getEndpoint(), result.getMessage() );
            return result;
        }

        Collection<RunnerFig> runnerFigs;
        runnerFigs = new ArrayList<RunnerFig>( 1 );
        runnerFigs.add( runnerFig );
        LinkedList<RunnerFig> failed = new LinkedList<RunnerFig>( runnerFigs );

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
                return new BaseResult( runnerFig.getUrl() + "/load", false,
                        "failed to verify all nodes as ready", State.INACTIVE );
            }

            RunnerFig runnerFigInfo = failed.removeLast();

            try {
                Result status = status( runnerFigInfo );
                ProjectFig remoteInfo = status.getProject();

                if ( ! status.getStatus() ) {
                    LOG.info( "RunnerFig {} failed on status call", runnerFigInfo );
                    failed.addFirst( runnerFigInfo );
                }
                else if ( status.getState() != State.READY ) {
                    LOG.info( "RunnerFig {} not yet in READY state", runnerFigInfo );
                    failed.addFirst( runnerFigInfo );
                }
                else if ( remoteInfo == null || remoteInfo.getWarMd5() == null ) {
                    failed.addFirst( runnerFigInfo );
                }
                else if ( ! remoteInfo.getWarMd5().equals( md5 ) ) {
                    throw new RuntimeException( "RunnerFig at " + runnerFigInfo.getHostname() +
                            " has wrong md5, was expecting " + md5 );
                }
                else {
                    LOG.info( "RunnerFig {} is backup and READY!", runnerFigInfo );
                }
            }
            catch ( Exception e ) {
                LOG.debug( "RunnerFig {} failed on status call", runnerFigInfo, e );
                failed.addFirst( runnerFigInfo );
            }
        }

        return result;
    }


    private ProjectFig getProject( final String testKey ) {
        return service.getProject( testKey );
    }


    @Override
    public Result status( RunnerFig runner ) {
        return RestRequests.status( runner );
    }


    /**
     * Sends the start rest request to runner.
     *
     * @param runner the runner configuration
     * @return the result
     */
    @Override
    public Result start( RunnerFig runner ) {
        return RestRequests.start( runner );
    }


    /**
     * Checks if the given runnerFig is in RUNNING state. If that check succeeds, sends the stop rest request
     * @param runnerFig the runner configuration
     * @return the result
     */
    @Override
    public Result stop( final RunnerFig runnerFig ) {
        Result status;
        boolean stoppable = false;

        status = status( runnerFig );
        if ( status.getStatus() && status.getState() == State.RUNNING ) {
            stoppable = true;
        }

        if ( ! stoppable ) {
            LOG.info( "Runner is not in a stoppable state" );
            return new BaseResult( status.getEndpoint(), false, "Cannot stop", status.getState() );
        }

        LOG.info( "Sending stop request to runnerFig at {}", runnerFig.getHostname() );
        return RestRequests.stop( runnerFig );
    }


    /**
     * Checks if the given runnerFig is in STOPPED state. If that check succeeds, sends the reset
     * rest request.
     *
     * @param runnerFig the runner configuration
     * @return the result
     */
    @Override
    public Result reset( final RunnerFig runnerFig ) {
        Result status = status( runnerFig );
        boolean resettable = ( status.getStatus() && status.getState() == State.STOPPED );

        if ( ! resettable ) {
            LOG.info( "Runner is not in a resettable state" );
            return new BaseResult( status.getEndpoint(), false, "Cannot reset", status.getState() );
        }

        LOG.info( "Sending reset request to runnerFig at {}", runnerFig.getHostname() );
        return RestRequests.reset( runnerFig );
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
        ProjectFig project = null;
        try {
            Set<ProjectFig> projects = getProjectConfigs();

            for ( ProjectFig projectCandidate : projects ) {
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

        Collection<RunnerFig> runnerFigs = getRunners();
        for ( RunnerFig runnerFig : runnerFigs ) {
            try {
                LOG.info( "Verifying runnerFig with hostname {}", runnerFig.getHostname() );
                Result result = status( runnerFig );

                if ( ! result.getStatus() ) {
                    LOG.info( "State of runnerFig could not be retrieved" );
                    LOG.info( "RunnerFig hostname: {}", runnerFig.getHostname() );
                    return result;
                }
                if ( ! result.getState().accepts( Signal.START, State.RUNNING ) ) {
                    LOG.info( "RunnerFig is not in a ready state, State: {}", result.getState() );
                    LOG.info( "RunnerFig hostname: {}", runnerFig.getHostname() );
                    return result;
                }
                if ( ! result.getProject().getWarMd5().equals( project.getWarMd5() ) ) {
                    message = "RunnerFig doesn't have the latest test loaded";
                    LOG.info( message );
                    LOG.info( "RunnerFig hostname: {}", runnerFig.getHostname() );
                    LOG.info( "Latest test MD5 is {}", project.getWarMd5() );
                    LOG.info( "RunnerFig's installed MD5 is {}", result.getProject().getWarMd5() );
                    return new BaseResult( result.getEndpoint(), false, message, result.getState() );
                }
                LOG.info( "RunnerFig is READY: {}", runnerFig );
            } catch ( Exception e ) {
                message = "Error while getting runner states";
                LOG.warn( message, e );
                return new BaseResult( runnerFig.getHostname(), false, message, State.INACTIVE );
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
    public RunnerFig getLiveRunner() {
        Collection<RunnerFig> candidates = getRunners();

        for ( RunnerFig runnerFig : candidates )
        {
            try {
                Result result = status( runnerFig );

                if ( result.getStatus() && result.getState().accepts( Signal.LOAD, State.READY ) )
                {
                    return runnerFig;
                }
            }
            catch ( Exception e ) {
                LOG.warn( "RunnerFig {} does not seem to be responding.", runnerFig );
            }
        }

        LOG.error( "Could not find a live runner: blowing chunks!" );
        throw new RuntimeException( "No live drivers available" );
    }
}
