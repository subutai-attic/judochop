/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.chop.runner.rest;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.runner.IController;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.PropagatedResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.StoreService;
import org.safehaus.chop.runner.ServletFig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;


/** Loads a test configuration from the "tests" container. */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/load")
public class LoadResource extends PropagatingResource {
    private static final Logger LOG = LoggerFactory.getLogger( LoadResource.class );
    private static final String PARAM_PROPAGATE = "propagate";

    private final IController controller;
    private final ProjectFig projectFig;
    private final ServletFig servletFig;


    @Inject
    public LoadResource( IController controller, StoreService service, ProjectFig projectFig, ServletFig servletFig ) {
        super( "/load", service );
        this.controller = controller;
        this.projectFig = projectFig;
        this.servletFig = servletFig;
    }


    /**
     * By default the propagate parameter is considered to be false unless set to true. To propagate this call to all
     * the other drivers this parameter will be set to true.
     *
     * @param propagate when true call the same function on other drivers
     * @param project the project to use specified by the string containing the <git-uuid>-<deploy-timestamp>
     *
     * @return a summary message
     */
    @POST
    public Result load( @QueryParam( PARAM_PROPAGATE ) Boolean propagate, @QueryParam( Constants.PARAM_PROJECT ) String project ) {
        LOG.debug( "The propagate request parameter was set to {}", propagate );

        if ( controller.isRunning() ) {
            return new BaseResult( getEndpointUrl(), false, "still running stop and reset before loading a new test",
                    controller.getState() );
        }

        if ( controller.needsReset() ) {
            return new BaseResult( getEndpointUrl(), false, "reset before loading a new test", controller.getState() );
        }

        Map<String, Runner> peers = getService().getRunners();

        // Handle loading the war here first for the peers we will propagate to since
        // we do not want to be reloaded before issuing this operation to the other drivers.

        Map<String, String> params = Collections.singletonMap( Constants.PARAM_PROJECT, project );

        if ( propagate == Boolean.TRUE ) {
            PropagatedResult result =
                    propagate( controller.getState().next( Signal.LOAD ), true, "reload started", params );

            try {
                deploy( project );
                result.setStatus( true );
                return result;
            }
            catch ( Exception e ) {
                LOG.error( "Encountered failure while reloading project", e );
                result.setStatus( false );
                result.setMessage( e.getMessage() );
                return result;
            }
        }

        for ( Runner runner : peers.values() ) {

        }

        try {
            deploy( project );
            return new BaseResult( getEndpointUrl(), true, "reload started", controller.getState().next( Signal.LOAD ) );
        }
        catch ( Exception e ) {
            LOG.error( "Encountered failure while reloading project", e );
            return new BaseResult( getEndpointUrl(), false, e.getMessage(), controller.getState() );
        }
    }


    private void deploy( String project ) throws Exception {
        // @Todo if the admin app reload does not work, do not store in app's temp are but in /tmp instead
        File tempDir = new File( getService().getMyMetadata().getTempDir() );
        File tempFile = getService().download( tempDir, project );
        final BlockingDeployTask uploadTask = new BlockingDeployTask( tempFile );
        new Thread( uploadTask ).start();
        uploadTask.returnOnLimit();   // ==> blocks until we hit the limit

        // Now we create and launch a thread that unblocks the task after
        // we have long since responded back our tracker to the client
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 2000L );
                }
                catch ( InterruptedException e ) {
                    LOG.error( "Interrupted from my sleep." );
                }

                uploadTask.unblock();
            }
        } ).start();
    }


    @Override
    protected Callable<Result> getRecoveryOperation( final PropagatingCall failingCaller ) {
        return new RecoveryOperation( failingCaller );
    }


    /**
     * Will try to recover by confirming that the peer is back up by attempting to contact the reloaded peer N number of
     * times with X seconds delay in between on it's /status page. The version of the project will be checked against
     * the project that was loaded in the /load operation that failed.
     */
    class RecoveryOperation implements Callable<Result> {
        private final PropagatingCall failingCaller;


        public RecoveryOperation( PropagatingCall failingCaller ) {
            this.failingCaller = failingCaller;
        }


        @Override
        public Result call() throws Exception {
            Exception lastException = null;

            for ( int retryCount = 0; retryCount < servletFig.getRecoveryRetryCount(); retryCount++ ) {
                if ( servletFig.getRetryDelay() > 0 ) {
                    try {
                        Thread.sleep( servletFig.getRetryDelay() );
                    }
                    catch ( InterruptedException e ) {
                        LOG.warn( "Got interrupted on recover retry sleep for delay. "
                                + "Retry operation will happen less than expected delay" );
                    }
                }

                try {
                    Result result = checkStatus();
                    LOG.info( "Retry SUCCESS! {}", result );
                    return result;
                }
                catch ( Exception e ) {
                    LOG.warn( "The {}th retry failed." );
                    lastException = e;
                }
            }

            if ( lastException != null ) {
                throw lastException;
            }

            String errMsg = "The recovery operation should never really get here.";
            LOG.error( errMsg );
            throw new Exception( errMsg );
        }


        private Result checkStatus() throws Exception {
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            Client client = Client.create( clientConfig );
            WebResource resource = client.resource( failingCaller.getMetadata().getUrl() ).path( "/status" );
            return resource.accept( MediaType.APPLICATION_JSON_TYPE ).get( BaseResult.class );
        }
    }


    class BlockingDeployTask implements Runnable {
        private final LockableInputStream in;
        private final String contentDisposition;
        private Result result;


        BlockingDeployTask( File war ) throws IOException {
            in = new LockableInputStream( war, war.length() - 5000 );
            contentDisposition = "attachment; filename=\"" + war.getName() + "\"";
        }


        public Result getResult() {
            return result;
        }


        public void unblock() {
            in.deactivateLimit();
        }


        public void returnOnLimit() throws InterruptedException {
            in.returnOnLimit();
        }


        public void run() {
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            Client client = Client.create( clientConfig );
            client.addFilter( new HTTPBasicAuthFilter( projectFig.getManagerUsername(),
                    projectFig.getManagerPassword() ) );

            WebResource resource = client.resource( servletFig.getManagerEndpoint() ).path( "/deploy" )
                                         .queryParam( "update", "true" ).queryParam( "path", "/" );

            // We will block on the put upload at just a little before the end
            String response = resource.type( MediaType.APPLICATION_OCTET_STREAM_TYPE )
                                      .header( "Content-Disposition", contentDisposition ).accept( "text/plain" )
                                      .put( String.class, in );

            if ( response.contains( "FAIL" ) ) {
                LOG.error( "FAILED to deploy via tomcat manager: {}", response );
                result = new BaseResult( getEndpointUrl(), false, response, controller.getState() );
            }
            else if ( response.contains( "OK" ) ) {
                LOG.info( "SUCCEEDED to deploy via tomcat manager: {}", response );
                result = new BaseResult( getEndpointUrl(), true, response, State.READY );
            }

            LOG.warn( "Got back unknown response from the manager: {}", response );
            result = new BaseResult( getEndpointUrl(), false, response, State.INACTIVE );
        }
    }
}
