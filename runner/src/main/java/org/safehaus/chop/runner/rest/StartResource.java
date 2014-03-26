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

import org.safehaus.chop.runner.IController;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
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

    public static final String ALREADY_RUNNING_MESSAGE = "Cannot start when already running.";
    public static final String TEST_MESSAGE = "/start resource called in test mode";
    public static final String TEST_PARAM = "test";
    private static final String RESET_NEEDED_MESSAGE = "A reset is need before starting.";


    private final IController controller;


    @Inject
    public StartResource( IController runner ) {
        this.controller = runner;
    }


    @POST
    public Response start( @QueryParam( TEST_PARAM ) boolean test )
    {
        if ( test ) {
            return Response.status( Response.Status.OK ).entity( TEST_MESSAGE ).build();
        }

        if ( controller.isRunning() ) {
            LOG.warn( ALREADY_RUNNING_MESSAGE );
            return Response.status( Response.Status.CONFLICT ).entity( ALREADY_RUNNING_MESSAGE ).build();
        }

        if ( controller.needsReset() ) {
            LOG.warn( RESET_NEEDED_MESSAGE );
            return Response.status( Response.Status.CONFLICT ).entity( RESET_NEEDED_MESSAGE ).build();
        }

        controller.start();
        return Response.status( Response.Status.OK ).entity( Boolean.TRUE ).build();
    }
}
