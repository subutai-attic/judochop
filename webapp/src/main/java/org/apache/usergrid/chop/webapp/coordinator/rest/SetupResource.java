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


import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.usergrid.chop.api.RestParams;
import org.apache.usergrid.chop.webapp.dao.UserDao;
import org.apache.usergrid.chop.stack.Stack;

import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private UserDao userDao;


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
            LOG.warn( "Calling /setup/stack" );
        }



        return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
    }
}
