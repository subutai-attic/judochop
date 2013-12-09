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
package org.safehaus.perftest.server.rest;


import org.safehaus.perftest.api.BaseResult;
import org.safehaus.perftest.PerftestRunner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * ...
 */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( "/reset" )
public class ResetResource extends PropagatingResource {
    private static final Logger LOG = LoggerFactory.getLogger( ResetResource.class );
    private final PerftestRunner runner;


    @Inject
    public ResetResource( PerftestRunner runner, StoreService service ) {
        super( "/reset", service );
        this.runner = runner;
    }


    /**
     * By default the propagate parameter is considered to be false unless set
     * to true. To propagate this call to all the other runners this parameter
     * will be set to true.
     *
     * @param propagate when true call the same function on other runners
     * @return a summary message
     */
    @POST
    public Result reset( @QueryParam( "propagate" ) Boolean propagate ) {
        LOG.debug( "The propagate request parameter was set to {}", propagate );

        if ( runner.isRunning() ) {
            return new BaseResult( getEndpointUrl(), false, "still running stop before resetting", runner.getState() );
        }

        if ( runner.needsReset() ) {
            runner.reset();

            if ( propagate == Boolean.FALSE ) {
                return new BaseResult( getEndpointUrl(), true, "reset complete", runner.getState() );
            }

            return propagate( runner.getState(), true, "reset complete" );
        }

        return new BaseResult( getEndpointUrl(), false, "reset not required", runner.getState() );
    }
}
