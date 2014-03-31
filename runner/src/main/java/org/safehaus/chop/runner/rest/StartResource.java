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


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.runner.IController;
import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/** ... */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( StartResource.ENDPOINT_URL )
public class StartResource {
    private static final Logger LOG = LoggerFactory.getLogger( StartResource.class );
    public static final String ENDPOINT_URL = "/start";

    public static final String TEST_PARAM = TestMode.TEST_MODE_PROPERTY;
    public static final String SUCCESS_MESSAGE = "Controller started executing tests.";
    public static final String ALREADY_RUNNING_MESSAGE = "Cannot start when already running.";
    public static final String TEST_MESSAGE = "/start resource called in test mode";
    private static final String RESET_NEEDED_MESSAGE = "A reset is need before starting.";


    private final IController controller;
    private final Project project;


    @Inject
    public StartResource( IController runner, Project project ) {
        this.controller = runner;
        this.project = project;
    }


    @POST
    @Produces( MediaType.APPLICATION_JSON )
    public Response start( @QueryParam( TEST_PARAM ) String test )
    {
        BaseResult result = new BaseResult();
        result.setProject( project );
        result.setEndpoint( ENDPOINT_URL );

        if ( test != null && ( test.equals( TestMode.INTEG.toString() ) || test.equals( TestMode.UNIT.toString() ) ) )
        {
            result.setStatus( true );
            result.setMessage( TEST_MESSAGE );
            LOG.info( TEST_MESSAGE );
            return Response.ok( result, MediaType.APPLICATION_JSON_TYPE ).build();
        }

        if ( controller.isRunning() ) {
            result.setStatus( false );
            result.setMessage( ALREADY_RUNNING_MESSAGE );
            LOG.warn( ALREADY_RUNNING_MESSAGE );
            return Response.status( Response.Status.CONFLICT ).entity( result ).build();
        }

        if ( controller.needsReset() ) {
            result.setStatus( false );
            result.setMessage( RESET_NEEDED_MESSAGE );
            LOG.warn( RESET_NEEDED_MESSAGE );
            return Response.status( Response.Status.CONFLICT ).entity( result ).build();
        }

        controller.start();
        result.setStatus( true );
        result.setMessage( SUCCESS_MESSAGE );
        LOG.info( SUCCESS_MESSAGE );
        return Response.status( Response.Status.OK ).entity( result ).build();
    }
}
