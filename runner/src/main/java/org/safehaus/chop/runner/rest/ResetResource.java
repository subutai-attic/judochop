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


import javax.annotation.Nullable;
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
@Path( ResetResource.ENDPOINT_URL )
public class ResetResource {
    public final static String ENDPOINT_URL = "/reset";
    private static final Logger LOG = LoggerFactory.getLogger( ResetResource.class );
    private final IController runner;


    @Inject
    private Project project;

    @Inject
    public ResetResource( IController runner ) {
        this.runner = runner;
    }


    @POST
    public Response reset( @QueryParam( TestMode.TEST_MODE_PROPERTY ) @Nullable String testMode ) {
        if ( testMode != null && ( testMode.equals( TestMode.INTEG.toString() )
                || testMode.equals( TestMode.UNIT.toString() ) ) ) {
            BaseResult result = new BaseResult( ENDPOINT_URL, false, "called in test mode: " + testMode,
                    runner.getState() );
            result.setProject( project );
            return Response.status( Response.Status.OK ).entity( result ).build();
        }

        if ( runner.isRunning() ) {
            BaseResult result = new BaseResult( ENDPOINT_URL, false, "still running stop before resetting",
                    runner.getState() );
            result.setProject( project );
            return Response.status( Response.Status.CONFLICT ).entity( result ).build();
        }

        if ( runner.needsReset() ) {
            runner.reset();

            BaseResult result = new BaseResult( ENDPOINT_URL, true, "resetting", runner.getState() );
            result.setProject( project );
            return Response.status( Response.Status.OK ).entity( result ).build();
        }

        LOG.warn( "Calling reset is not needed." );
        BaseResult result = new BaseResult( ENDPOINT_URL, false, "reset not required", runner.getState() );
        result.setProject( project );
        return Response.status( Response.Status.CONFLICT ).entity( result ).build();
    }
}
