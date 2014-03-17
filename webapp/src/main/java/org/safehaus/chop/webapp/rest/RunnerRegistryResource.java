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
package org.safehaus.chop.webapp.rest;


import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.dao.RunnerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * REST operation to setup the Stack under test.
 */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( RunnerRegistryResource.ENDPOINT_URL )
public class RunnerRegistryResource {
    public final static String ENDPOINT_URL = "/runners";
    private static final Logger LOG = LoggerFactory.getLogger( RunnerRegistryResource.class );


    @Inject
    private RunnerDao runnerDao;


    @GET
    @Path( "/list" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response list( @QueryParam(  RestParams.COMMIT_ID ) String commitId ) throws Exception {
        LOG.warn( "Calling list ..." );
        List<Runner> runnerList = runnerDao.getRunners( commitId );
        Runner[] runners = new Runner[runnerList.size()];
        return Response.status( Response.Status.CREATED ).entity( runnerList.toArray( runners ) ).build();
    }


    @POST
    @Path( "/register" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.TEXT_PLAIN )
    public Response register( @QueryParam( RestParams.COMMIT_ID ) String commitId, Runner runner ) throws Exception
    {
        LOG.warn( "Calling register ..." );

        if ( runnerDao.save( runner, commitId ) ) {
            LOG.info( "registered runner {}", runner.getHostname() );
            return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
        }
        else {
            LOG.warn( "failed to register runner {}", runner.getHostname() );
            return Response.status( Response.Status.CREATED ).entity( "FALSE" ).build();
        }
    }


    @POST
    @Path( "/unregister" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.TEXT_PLAIN )
    public Response unregister( @QueryParam( RestParams.RUNNER_HOSTNAME ) String runnerHostname )
    {
        LOG.warn( "Calling unregister ..." );

        if ( runnerDao.delete( runnerHostname ) ) {
            LOG.info( "unregistered runner {}", runnerHostname );
            return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
        }
        else {
            LOG.warn( "failed to unregister runner {}", runnerHostname );
            return Response.status( Response.Status.CREATED ).entity( "FALSE" ).build();
        }
    }
}
