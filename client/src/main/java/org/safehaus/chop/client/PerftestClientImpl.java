package org.safehaus.chop.client;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
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
import org.safehaus.chop.api.store.amazon.AmazonFig;
import org.safehaus.chop.client.rest.RestRequests;
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
public class PerftestClientImpl implements PerftestClient, Constants {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientImpl.class );
    private final StoreService service;
    @Inject @Named( AmazonFig.AWS_BUCKET_KEY ) private DynamicStringProperty awsBucket;


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
        return null;
    }


    @Override
    public File getResults( final ISummary run ) throws IOException {
        return File.createTempFile( "foo", "bar" );
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
    public Result load( RunnerFig runnerFig, String projectKey, Boolean propagate ) {
        ProjectFig project = getProject( projectKey );
        String md5 = project.getWarMd5();

        LOG.warn( "Sending load request to " + runnerFig.getHostname() );

        Result result = RestRequests.load( runnerFig, projectKey, propagate );

        if ( ! result.getStatus() ) {
            LOG.info( "Failed on load POST to {} response message was: {}" ,
                    result.getEndpoint(), result.getMessage()  );
            return result;
        }

        Collection<RunnerFig> runnerFigs = getRunners();
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
                    LOG.warn( "RunnerFig {} failed on status call", runnerFigInfo );
                    failed.addFirst( runnerFigInfo );
                }

                if ( status.getState() != State.READY ) {
                    LOG.warn( "RunnerFig {} not yet in READY state", runnerFigInfo );
                    failed.addFirst( runnerFigInfo );
                }

                if ( remoteInfo != null && remoteInfo.getWarMd5() != null ) {
                    if ( ! remoteInfo.getWarMd5().equals( md5 ) ) {
                        LOG.warn( "RunnerFig {} has wrong md5 ... was expecting {}", runnerFigInfo, md5 );
                        failed.addFirst( runnerFigInfo );
                    }
                }

                LOG.info( "RunnerFig {} is backup and READY!", runnerFigInfo );
            }
            catch ( Exception e ) {
                LOG.warn( "RunnerFig {} failed on status call", runnerFigInfo, e );
                failed.addFirst( runnerFigInfo );
            }
        }

        return result;
    }


    private ProjectFig getProject( final String testKey ) {
        return service.getProject( testKey );
    }


    @Override
    public Result status( RunnerFig runnerFig ) {
        return RestRequests.status( runnerFig );
    }


    /**
     * Sends the start rest request to runnerFig with given propagate value.
     * Call verify() method first to make sure all drivers are ready and up-to-date, call status() if you're not
     * going to propagate.
     * @param runnerFig
     * @param propagate
     * @return
     */
    @Override
    public Result start( RunnerFig runnerFig, final boolean propagate ) {
        return RestRequests.start( runnerFig, propagate );
    }


    /**
     * If propagate is true, checks if there is at least one runnerFig in cluster in RUNNING state; if not, checks
     * if the given runnerFig is in RUNNING state. If that check succeeds, sends the stop rest request
     * @param runnerFig
     * @param propagate
     * @return
     */
    @Override
    public Result stop( final RunnerFig runnerFig, final boolean propagate ) {
        Result status = null;
        boolean stoppable = false;
        // if request is wished to be propagated, then we check if there is at least one runnerFig in RUNNING state
        if ( propagate ) {
            Collection<RunnerFig> runnerFigs = getRunners();
            for ( RunnerFig r : runnerFigs ) {
                status = status( r );
                if ( status.getStatus() && status.getState() == State.RUNNING ) {
                    stoppable = true;
                    break;
                }
            }
        }
        else {
            status = status( runnerFig );
            if ( status.getStatus() && status.getState() == State.RUNNING ) {
                stoppable = true;
            }
        }

        if ( ! stoppable ) {
            LOG.info( "Cluster is not in a stoppable state" );
            return new BaseResult( status.getEndpoint(), true, "Cannot stop", status.getState() );
        }

        LOG.info( "Sending stop request to runnerFig at {}", runnerFig.getHostname() );
        return RestRequests.stop( runnerFig, propagate );
    }


    /**
     * If propagate is true, checks if there is at least one runnerFig in cluster in STOPPED state; if not, checks
     * if the given runnerFig is in STOPPED state. If that check succeeds, sends the reset rest request
     * @param runnerFig
     * @param propagate
     * @return
     */
    @Override
    public Result reset( final RunnerFig runnerFig, final boolean propagate ) {
        Result status = status( runnerFig );
        boolean resettable = false;
        // if request is wished to be propagated, then we check if there is at least one runnerFig in STOPPED state
        if ( propagate ) {
            Collection<RunnerFig> runnerFigs = getRunners();
            for ( RunnerFig r : runnerFigs ) {
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

        LOG.info( "Sending reset request to runnerFig at {}", runnerFig.getHostname() );
        return RestRequests.reset( runnerFig, propagate );
    }


    @Override
    public Result scan( final RunnerFig runnerFig, final boolean propagate ) {
        return new BaseResult( "http://localhost:8080", true, "scan triggered", State.READY );
    }


    /**
     * Checks whether all the drivers in the cluster are reachable, in a READY state
     * and has the latest test with matching MD5 checksum fields.
     * @return Returns true if all instances on the cluster is ready to start the test
     */
    @Override
    public boolean verify() {
        LOG.info( "Starting verify operation..." );

        // Get the latest project information
        ProjectFig project = null;
        try {
            Set<ProjectFig> projects = getProjectConfigs();

            for ( ProjectFig projectCandidate : projects ) {
                if ( project == null ) {
                    project = projectCandidate;
                }
                else if ( compareTimestamps( projectCandidate.getCreateTimestamp(), project.getCreateTimestamp() ) > 0 ) {
                    project = projectCandidate;
                }
            }
        } catch ( Exception e ) {
            LOG.warn( "Error while getting project information from store", e );
            return false;
        }

        if ( project == null ) {
            LOG.info( "No projects were found in the store" );
            return false;
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
                    return false;
                }
                if ( result.getState().accepts( Signal.LOAD, State.READY ) ) {
                    LOG.info( "RunnerFig is not in a ready state, State: {}", result.getState() );
                    LOG.info( "RunnerFig hostname: {}", runnerFig.getHostname() );
                    return false;
                }
                if ( ! result.getProject().getWarMd5().equals( project.getWarMd5() ) ) {
                    LOG.info( "RunnerFig doesn't have the latest test loaded" );
                    LOG.info( "RunnerFig hostname: {}", runnerFig.getHostname() );
                    LOG.info( "Latest test MD5 is {}", project.getWarMd5() );
                    LOG.info( "RunnerFig's installed MD5 is {}", result.getProject().getWarMd5() );
                    return false;
                }
                LOG.info( "RunnerFig is READY: {}", runnerFig );
            } catch ( Exception e ) {
                LOG.warn( "Error while getting runnerFig states", e );
                return false;
            }
        }

        LOG.info( "Cluster is ready to start..." );

        return true;
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
