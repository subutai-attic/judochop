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

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.Store;
import org.safehaus.chop.api.store.amazon.AmazonFig;
import org.safehaus.chop.runner.IController;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.safehaus.chop.runner.ServletFig;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import static org.safehaus.chop.api.Constants.PARAM_PROJECT;


/** Loads a test configuration from the "tests" container. */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( LoadResource.ENDPOINT_URL )
public class LoadResource {
    public static final String ENDPOINT_URL = "/load";
    private static final Logger LOG = LoggerFactory.getLogger( LoadResource.class );

    private final IController controller;
    private final Project project;
    private final ServletFig servletFig;
    private final Runner runner;
    private final Store service;


    @Inject
    public LoadResource( IController controller,
                         Store service,
                         Project project,
                         ServletFig servletFig,
                         Runner runner ) {
        this.service = service;
        this.controller = controller;
        this.project = project;
        this.servletFig = servletFig;
        this.runner = runner;
    }


    /**
     * Also applies supplied set of parameters to various configurations.
     *
     * @param project the project to use specified by the string containing the <git-uuid>-<deploy-timestamp>
     * @return a summary message
     */
    @POST
    public Result load( @QueryParam( PARAM_PROJECT ) String project,
                        @Context UriInfo uriInfo ) {

        if ( controller.isRunning() ) {
            return new BaseResult( ENDPOINT_URL, false, "still running stop and reset before loading a new test",
                    controller.getState() );
        }

        if ( controller.needsReset() ) {
            return new BaseResult( ENDPOINT_URL, false, "reset before loading a new test", controller.getState() );
        }

        if ( ! controller.getState().accepts( Signal.LOAD ) ) {
            LOG.error( "State of {} does not accept the LOAD signal.", controller.getState() );
            return new BaseResult( ENDPOINT_URL, false, "LOAD signal not accepted by state.", controller.getState() );
        }

        AmazonFig amazonFig = Guice.createInjector( new GuicyFigModule( AmazonFig.class ) )
                                   .getInstance( AmazonFig.class );

        // Apply every parameter that is either a key or method name in query parameters as a bypass
        MultivaluedMap<String,String> parameters = uriInfo.getQueryParameters();
        for ( String key : parameters.keySet() ) {
            if ( amazonFig.getOption( key ) != null ) {
                LOG.debug( "Applying parameter {} with value {} as bypass to amazonFig", key, parameters.get( key ) );
                amazonFig.bypass( key, parameters.get( key ).get( 0 ) );
            }

            if ( this.project.getOption( key ) != null ) {
                LOG.debug( "Applying parameter {} with value {} as bypass to project", key, parameters.get( key ) );
                this.project.bypass( key, parameters.get( key ).get( 0 ) );
            }
        }

        LOG.info( "About to shut down the store service." );
        service.stop();
        LOG.info( "About to restart the store service." );
        service.start();

        try {
            deploy( project );
            return new BaseResult( ENDPOINT_URL, true, "reload started", controller.getState().next( Signal.LOAD ) );
        }
        catch ( Exception e ) {
            LOG.error( "Encountered failure while reloading project", e );
            return new BaseResult( ENDPOINT_URL, false, e.getMessage(), controller.getState() );
        }
    }


    private void deploy( String project ) throws Exception {
        /*
         * We need to make sure that we store this new runner war version we pull
         * in to a place where it can be accessed properly by Tomcat's manager
         * application. The temp directory needs to be a reachable area which
         * is not the same for all the applications: some areas are blocked from
         * servlet access.
         */

        File tempDir = new File( runner.getTempDir() );
        File tempFile = service.download( tempDir, project );
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
            client.addFilter( new HTTPBasicAuthFilter( project.getManagerUsername(),
                    project.getManagerPassword() ) );

            WebResource resource = client.resource( servletFig.getManagerEndpoint() ).path( "/deploy" )
                                         .queryParam( "update", "true" ).queryParam( "path", "/" );

            // We will block on the put upload at just a little before the end
            String response = resource.type( MediaType.APPLICATION_OCTET_STREAM_TYPE )
                                      .header( "Content-Disposition", contentDisposition ).accept( "text/plain" )
                                      .put( String.class, in );

            if ( response.contains( "FAIL" ) ) {
                LOG.error( "FAILED to deploy via tomcat manager: {}", response );
                result = new BaseResult( ENDPOINT_URL, false, response, controller.getState() );
            }
            else if ( response.contains( "OK" ) ) {
                LOG.info( "SUCCEEDED to deploy via tomcat manager: {}", response );
                result = new BaseResult( ENDPOINT_URL, true, response, State.READY );
            }

            LOG.warn( "Got back unknown response from the manager: {}", response );
            result = new BaseResult( ENDPOINT_URL, false, response, State.INACTIVE );
        }
    }
}
