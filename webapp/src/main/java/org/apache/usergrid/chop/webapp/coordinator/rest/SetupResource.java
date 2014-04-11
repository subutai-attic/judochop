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
package org.apache.usergrid.chop.webapp.coordinator.rest;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.api.Constants;
import org.apache.usergrid.chop.api.Module;
import org.apache.usergrid.chop.api.RestParams;
import org.apache.usergrid.chop.stack.BasicStack;
import org.apache.usergrid.chop.stack.User;
import org.apache.usergrid.chop.webapp.ChopUiFig;
import org.apache.usergrid.chop.webapp.coordinator.StackCoordinator;
import org.apache.usergrid.chop.webapp.dao.CommitDao;
import org.apache.usergrid.chop.webapp.dao.ModuleDao;
import org.apache.usergrid.chop.webapp.dao.UserDao;
import org.apache.usergrid.chop.stack.Stack;
import org.apache.usergrid.chop.webapp.dao.model.BasicModule;

import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * REST operation to setup the Stack under test.
 */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( SetupResource.ENDPOINT)
public class SetupResource extends TestableResource implements RestParams {
    public final static String ENDPOINT = "/setup";
    private static final Logger LOG = LoggerFactory.getLogger( SetupResource.class );


    @Inject
    private StackCoordinator stackCoordinator;


    @Inject
    private ChopUiFig chopUiFig;


    @Inject
    private UserDao userDao;


    @Inject
    private CommitDao commitDao;


    @Inject
    private ModuleDao moduleDao;


    public SetupResource() {
        super( ENDPOINT );
    }

    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/stack" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response stack(
            @QueryParam( RestParams.COMMIT_ID ) String commitId,
            @QueryParam( RestParams.MODULE_ARTIFACTID ) String artifactId,
            @QueryParam( RestParams.MODULE_GROUPID ) String groupId,
            @QueryParam( RestParams.MODULE_VERSION ) String version,
            @QueryParam( RestParams.USERNAME ) String user,
            @QueryParam( RestParams.RUNNER_COUNT ) int runnerCount ,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode
                         )
    {
        if( inTestMode( testMode ) ) {
            LOG.info( "Calling /setup/stack in test mode ..." );
        }
        else {
            LOG.info( "Calling /setup/stack" );
        }
        String message;

        User chopUser = userDao.get( user );
        if( chopUser == null ) {
            message = "User " + user + " not found";
            LOG.warn( message );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
                           .entity( message )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        File runnerJar = new File( chopUiFig.getContextPath() );
        runnerJar = new File( runnerJar, user );
        runnerJar = new File( runnerJar, groupId );
        runnerJar = new File( runnerJar, artifactId );
        runnerJar = new File( runnerJar, version );
        runnerJar = new File( runnerJar, commitId );
        runnerJar = new File( runnerJar, Constants.RUNNER_JAR );
        if( ! runnerJar.exists() ) {
            message = "No runner jars have been found by these parameters, deploy first";
            LOG.warn( message );
            return Response.status( Response.Status.BAD_REQUEST )
                           .entity( message )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        Stack stack;
        try {
            // Access the jar file resources after adding it to a new ClassLoader
            URLClassLoader classLoader = new URLClassLoader( new URL[] { runnerJar.toURL() },
                    Thread.currentThread().getContextClassLoader() );
            ObjectMapper mapper = new ObjectMapper();
            stack = mapper.readValue( classLoader.getResourceAsStream( Constants.STACK_JSON ), BasicStack.class );
        }
        catch ( Exception e ) {
            message = "Error while reading stack.json from runner.jar resources";
            LOG.warn( message, e );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
                           .entity( message )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        Module module = moduleDao.get( BasicModule.createId( groupId, artifactId, version ) );
        if( module == null ) {
            message = "No registered modules found by " + groupId + ":" + artifactId + ":" + version;
            LOG.warn( message );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
                           .entity( message )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        Commit commit = null;
        for( Commit c: commitDao.getByModule( module.getId() ) ) {
            if( commitId.equals( c.getId() ) ) {
                commit = c;
                break;
            }
        }
        if( commit == null ) {
            message = "Commit with id " + commitId + " is not found";
            LOG.warn( message );
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR )
                           .entity( message )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        // TODO start setting up stack using StackCoordinator

        return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
    }
}
