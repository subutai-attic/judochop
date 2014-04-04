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
package org.safehaus.chop.webapp.coordinator.rest;


import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.indices.IndexMissingException;
import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.dao.RunnerDao;
import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * REST operation to setup the Stack under test.
 */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Consumes( MediaType.APPLICATION_JSON )
@Path( RunnerRegistryResource.ENDPOINT )
public class RunnerRegistryResource extends TestableResource {
    public final static String ENDPOINT = "/runners";
    private static final Logger LOG = LoggerFactory.getLogger( RunnerRegistryResource.class );


    @Inject
    private RunnerDao runnerDao;


    public RunnerRegistryResource() {
        super( ENDPOINT );
    }


    @GET
    @Path( "/list" )
    public Response list(

            @QueryParam(  RestParams.COMMIT_ID ) String commitId,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode

                        ) throws Exception
    {
        List<Runner> runnerList = Collections.emptyList();

        if ( inTestMode( testMode ) ) {
            LOG.info( "Calling /runners/list in test mode ..." );
            return Response.ok( runnerList ).build();
        }

        LOG.info( "Calling /runners/list ..." );
        Preconditions.checkNotNull( commitId, "The commitId must not be null." );

        try {
            runnerList = runnerDao.getRunners( "user", commitId, "moduleId" );
        }
        catch ( IndexMissingException e ) {
            LOG.warn( "Got a missing index exception. Returning empty list of Runners." );
        }

        Runner[] runners = new Runner[runnerList.size()];
        return Response.status( Response.Status.CREATED ).entity( runnerList.toArray( runners ) ).build();
    }


    @POST
    @Path( "/register" )
    @Produces( MediaType.APPLICATION_JSON )
    @Consumes( MediaType.APPLICATION_JSON )
    public Response register(

            @QueryParam( RestParams.COMMIT_ID ) String commitId,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode,
            Runner runner

                            ) throws Exception
    {
        if ( inTestMode( testMode ) ) {
            LOG.info( "Calling /runners/register in test mode ..." );
            return Response.ok( false ).build();
        }

        LOG.info( "Calling /runners/register with commitId = {} and runner = {}", commitId, runner );

        Preconditions.checkNotNull( commitId, "The commitId cannot be null." );
        Preconditions.checkNotNull( runner, "The runner cannot be null." );

        if ( runnerDao.save( runner, "user", commitId, "moduleId" ) ) {
            LOG.info( "registered runner {} for commit {}", runner.getHostname(), commitId );
            return Response.ok( true ).build();
        }
        else {
            LOG.warn( "failed to register runner {}", runner.getHostname() );
            return Response.ok( false ).build();
        }
    }


    @POST
    @Path( "/unregister" )
    public Response unregister(

            @QueryParam( RestParams.RUNNER_HOSTNAME ) String runnerHostname,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode

                              )
    {
        if ( inTestMode( testMode ) ) {
            LOG.info( "Calling /runners/unregister ..." );
            return Response.ok( false ).build();
        }


        Preconditions.checkNotNull( runnerHostname, "The runnerHostname cannot be null." );

        LOG.info( "Calling /runners/unregister ..." );
        try {
            if ( runnerDao.delete( runnerHostname ) ) {
                LOG.info( "unregistered runner {}", runnerHostname );
                return Response.ok( true ).build();
            }
            else {
                LOG.warn( "failed to unregister runner {}", runnerHostname );
                return Response.ok( false ).build();
            }
        }
        catch ( IndexMissingException e ) {
            LOG.warn( "Got missing index exception so returning false for unregister operation." );
            return Response.ok( false ).build();
        }
    }
}
