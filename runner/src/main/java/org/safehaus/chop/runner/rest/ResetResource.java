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

import org.safehaus.chop.runner.IController;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.safehaus.chop.api.Constants.PARAM_PROPAGATE;


/** ... */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/reset")
public class ResetResource extends PropagatingResource {
    private static final Logger LOG = LoggerFactory.getLogger( ResetResource.class );
    private final IController runner;


    @Inject
    public ResetResource( IController runner, StoreService service ) {
        super( "/reset", service );
        this.runner = runner;
    }


    /**
     * By default the propagate parameter is considered to be false unless set to true. To propagate this call to all
     * the other drivers this parameter will be set to true.
     *
     * @param propagate when true call the same function on other drivers
     *
     * @return a summary message
     */
    @POST
    public Result reset( @QueryParam( PARAM_PROPAGATE ) Boolean propagate ) {
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
