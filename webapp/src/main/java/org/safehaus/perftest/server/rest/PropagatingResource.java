package org.safehaus.perftest.server.rest;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.api.PropagatedResult;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.store.StoreService;
import org.safehaus.perftest.api.RunnerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;


/**
 * A resource that optionally propagates its operation to peer runners.
 */
public class PropagatingResource {
    private static final Logger LOG = LoggerFactory.getLogger( PropagatingResource.class );

    private final String resourcePath;
    private final String endpointUrl;
    private final ExecutorService executorService;
    private final StoreService service;


    protected PropagatingResource( String resourcePath, StoreService service ) {
        this.resourcePath = resourcePath;
        this.service = service;
        this.endpointUrl = service.getMyMetadata().getUrl() + resourcePath;
        executorService = Executors.newCachedThreadPool();
    }


    /**
     * Although this super class method Provides an optional recovery operation that can be executed when
     * propagation calls fail. The PropagationResource will only apply
     * recovery tactics if a non-null recovery operation is available.
     *
     * A good example when this mechanism is used is the /load operation
     * which will more often fail rather than succeed because when
     * restarting the application and responding there exists a race
     * condition. This recovery operation can be used to check that the
     * operation succeeded properly.
     *
     * @return a schedulable job to be performed on failure, or null
     */
    protected Callable<Result> getRecoveryOperation( @SuppressWarnings( "UnusedParameters" )
                                                     final PropagatingCall failingCaller )
    {
        return null;
    }


    /**
     * Gets the relative path to this resource.
     *
     * @return the relative path (minus hostname and port)
     */
    protected String getResourcePath() {
        return resourcePath;
    }


    /**
     * Gets the AmazonS3Service.
     *
     * @return the AmazonS3Service
     */
    protected StoreService getService() {
        return service;
    }


    /**
     * Gets the full http://hostname:port/foo/bar URL to the resource.
     *
     * @return the full resource URL
     */
    protected String getEndpointUrl() {
        return endpointUrl;
    }


    /**
     * Propagates this resource operation to other peers in the perftest cluster.
     *
     * @param status whether or not the propagating operation itself succeeded, sometimes you
     *               might want to propagate the operation even if the initiating operation failed
     * @param message the optional message to use if any
     * @return the results from the initiating peer and the remote peers.
     */
    protected PropagatedResult propagate( State state, boolean status, String message ) {
        return propagate( state, status, message, Collections.<String,String>emptyMap() );
    }


    /**
     * Propagates this resource operation to other peers in the perftest cluster.
     *
     * @param status whether or not the propagating operation itself succeeded, sometimes you
     *               might want to propagate the operation even if the initiating operation failed
     * @param message the optional message to use if any
     * @param params additional query parameters to pass-through to peers being propagated to
     * @return the results from the initiating peer and the remote peers.
     */
    protected PropagatedResult propagate( State state, boolean status, String message, final Map<String,String> params ) {
        PropagatedResult result = new PropagatedResult( getEndpointUrl(), status, message, state );
        BlockingQueue<Future<Result>> completionQueue = new LinkedBlockingQueue<Future<Result>>();
        ExecutorCompletionService<Result> completionService =
                new ExecutorCompletionService<Result>( executorService, completionQueue );

        for ( String runner : getService().listRunners() )
        {
            final RunnerInfo metadata = getService().getRunner( runner );

            // skip if the runner is myself
            if ( getService().getMyMetadata().getHostname().equals( metadata.getHostname() ) ) {
                continue;
            }

            completionService.submit( new PropagatingCall( metadata, params ) );
        }

        while ( ! completionQueue.isEmpty() ) {
            try {
                Future<Result> future = completionService.poll( 200, TimeUnit.MILLISECONDS );

                if ( future.isDone() || future.isCancelled() ) {
                    result.add( future.get() );
                }
            }
            catch ( InterruptedException e ) {
                LOG.error( "Interrupted while polling completionService.", e );
            }
            catch ( ExecutionException e ) {
                LOG.error( "Failure accessing Result from Future.", e );
            }
        }

        return result;
    }


    class PropagatingCall implements Callable<Result>
    {
        private final RunnerInfo metadata;
        private final Map<String,String> params;

        PropagatingCall( RunnerInfo metadata, Map<String,String> params ) {
            this.metadata = metadata;
            this.params = params;
        }

        @SuppressWarnings( "UnusedDeclaration" )
        RunnerInfo getMetadata() {
            return metadata;
        }

        @SuppressWarnings( "UnusedDeclaration" )
        Map<String,String> getParams() {
            return params;
        }

        @Override
        public Result call() throws Exception {
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            Client client = Client.create( clientConfig );
            WebResource resource = client.resource( metadata.getUrl() ).path( getResourcePath() );

            // Inject required query parameters
            resource = resource.queryParam( "propagate", "false" );
            for ( String paramKey : params.keySet() ) {
                if ( paramKey.equals( "propagate" ) ) {
                    continue;
                }

                resource = resource.queryParam( paramKey, params.get( paramKey ) );
            }

            Result remoteResult;

            try {
                remoteResult = resource.accept( MediaType.APPLICATION_JSON_TYPE ).post( BaseResult.class );
            }
            catch ( Exception e ) {
                LOG.error( "Failure on post to peer {}.", metadata.getHostname() );

                Callable<Result> recoveryOp = getRecoveryOperation( this );

                if ( recoveryOp == null )
                {
                    throw e;
                }

                try {
                    return recoveryOp.call();
                }
                catch ( Exception e2 ) {
                    LOG.error( "Failures encountered on recovery operation. Considering " +
                            "this propagating call to be a failure." );
                    return new BaseResult( getEndpointUrl(), false,
                            "Multiple failures encountered including on recovery operation!", null );
                }
            }

            return remoteResult;
        }
    }
}
