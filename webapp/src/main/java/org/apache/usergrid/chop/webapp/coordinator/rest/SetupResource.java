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
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.apache.usergrid.chop.stack.CoordinatedStack;
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

import org.apache.commons.lang.builder.HashCodeBuilder;

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


    public enum SetupStackStatus {
        SetUp, SettingUp, NotSetUp, NotFound
    }

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

    private Set<SetupStackThread> setUpStackThreads = Collections.newSetFromMap( new ConcurrentHashMap
            <SetupStackThread, Boolean>() );


    private Set<SetupStackThread> settingUpStackThreads = Collections.newSetFromMap( new ConcurrentHashMap
                <SetupStackThread, Boolean>() );


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
            @QueryParam( RestParams.RUNNER_COUNT ) int runnerCount,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode
                         )
    {
        if( inTestMode( testMode ) ) {
            LOG.info( "Calling /setup/stack in test mode ..." );
        }
        else {
            LOG.info( "Calling /setup/stack" );
        }

        SetupStackThread setupStack = getSetupStackThread( commitId, artifactId, groupId, version, user, runnerCount );
        SetupStackStatus status = stackStatus( setupStack );

        if( status.equals( SetupStackStatus.NotFound ) ) {
            return Response.status( Response.Status.BAD_REQUEST )
                           .entity( setupStack.getErrorMessage() )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }
        if( status.equals( SetupStackStatus.SettingUp ) ) {
            return Response.status( Response.Status.OK )
                           .entity( "Setting up" )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }
        if( status.equals( SetupStackStatus.SetUp ) ) {
            return Response.status( Response.Status.OK )
                           .entity( "Already set up" )
                           .type( MediaType.TEXT_PLAIN )
                           .build();
        }

        settingUpStackThreads.add( setupStack );
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit( setupStack );

        return Response.status( Response.Status.CREATED )
                       .entity( "Started setting up the stack" )
                       .type( MediaType.TEXT_PLAIN )
                       .build();
    }


    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Path( "/status" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response status(
            @QueryParam( RestParams.COMMIT_ID ) String commitId,
            @QueryParam( RestParams.MODULE_ARTIFACTID ) String artifactId,
            @QueryParam( RestParams.MODULE_GROUPID ) String groupId,
            @QueryParam( RestParams.MODULE_VERSION ) String version,
            @QueryParam( RestParams.USERNAME ) String user,
            @Nullable @QueryParam( TestMode.TEST_MODE_PROPERTY ) String testMode
                         )
    {
        if( inTestMode( testMode ) ) {
            LOG.info( "Calling /setup/status in test mode ..." );
        }
        else {
            LOG.info( "Calling /setup/status" );
        }

        SetupStackStatus status = stackStatus( commitId, artifactId, groupId, version, user, 0 );

        return Response.status( Response.Status.OK )
                       .entity( status )
                       .type( MediaType.TEXT_PLAIN )
                       .build();
    }


    private SetupStackStatus stackStatus( String commitId, String artifactId, String groupId, String version,
                                          String user, int runnerCount ) {

        SetupStackThread setupStackThread = getSetupStackThread( commitId, artifactId, groupId, version, user,
                runnerCount );

        return stackStatus( setupStackThread );
    }


    private SetupStackStatus stackStatus( SetupStackThread setupStackThread ) {
        if( setupStackThread.getErrorMessage() != null ) {
            return SetupStackStatus.NotFound;
        }
        if( setUpStackThreads.contains( setupStackThread ) ) {
            return SetupStackStatus.SetUp;
        }
        if( settingUpStackThreads.contains( setupStackThread ) ) {
            return SetupStackStatus.SettingUp;
        }
        return SetupStackStatus.NotSetUp;
    }


    private SetupStackThread getSetupStackThread( String commitId, String artifactId, String groupId, String version,
                                                  String user, int runnerCount ) {

        User chopUser = userDao.get( user );
        if( chopUser == null ) {
            return new SetupStackThread( "User " + user + " not found" );
        }

        File runnerJar = new File( chopUiFig.getContextPath() );
        runnerJar = new File( runnerJar, user );
        runnerJar = new File( runnerJar, groupId );
        runnerJar = new File( runnerJar, artifactId );
        runnerJar = new File( runnerJar, version );
        runnerJar = new File( runnerJar, commitId );
        runnerJar = new File( runnerJar, Constants.RUNNER_JAR );
        if( ! runnerJar.exists() ) {
            return new SetupStackThread( "No runner jars have been found by these parameters, deploy first" );
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
            return new SetupStackThread( "Error while reading stack.json from runner.jar resources" );
        }

        Module module = moduleDao.get( BasicModule.createId( groupId, artifactId, version ) );
        if( module == null ) {
            return new SetupStackThread( "No registered modules found by " + groupId + ":" + artifactId + ":" +
                    version );
        }

        Commit commit = null;
        for( Commit c: commitDao.getByModule( module.getId() ) ) {
            if( commitId.equals( c.getId() ) ) {
                commit = c;
                break;
            }
        }
        if( commit == null ) {
            return new SetupStackThread( "Commit with id " + commitId + " is not found" );
        }

        return new SetupStackThread( stack, chopUser, commit, module, runnerCount );
    }


    private class SetupStackThread implements Callable<CoordinatedStack> {

        private Stack stack;
        private User user;
        private Commit commit;
        private Module module;
        private int runnerCount;
        private String errorMessage;


        public SetupStackThread( Stack stack, User user, Commit commit, Module module, int runnerCount ) {
            this.stack = stack;
            this.user = user;
            this.commit = commit;
            this.module = module;
            this.runnerCount = runnerCount;
        }


        public SetupStackThread( String errorMessage ) {
            this.errorMessage = errorMessage;
        }


        public Stack getStack() {
            return stack;
        }


        public User getUser() {
            return user;
        }


        public Commit getCommit() {
            return commit;
        }


        public Module getModule() {
            return module;
        }


        public int getRunnerCount() {
            return runnerCount;
        }


        public String getErrorMessage() {
            return errorMessage;
        }


        @Override
        public CoordinatedStack call() throws Exception {
            // TODO put this in try catch and cascade errors
            CoordinatedStack result = stackCoordinator.setupStack( stack, user, commit, module, runnerCount );
            settingUpStackThreads.remove( this );
            setUpStackThreads.add( this );
            return result;
        }


        @Override
        public int hashCode() {
            if( errorMessage != null ) {
                return new HashCodeBuilder( 97, 71 )
                        .append( errorMessage )
                        .toHashCode();
            }
            return new HashCodeBuilder( 97, 71 )
                    .append( stack.getId().toString() )
                    .append( user.getUsername() )
                    .append( commit.getId() )
                    .append( module.getId() )
                    .toHashCode();
        }


        @Override
        public boolean equals( final Object obj ) {
            if( this == obj ) {
                return true;
            }
            if( obj == null ) {
                return false;
            }
            if ( ! ( obj instanceof SetupStackThread ) ) {
                return false;
            }
            return obj.hashCode() == this.hashCode();
        }
    }
}
