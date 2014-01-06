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

import org.safehaus.chop.api.Constants;
import org.safehaus.chop.runner.IController;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/** ... */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/start")
public class StartResource extends PropagatingResource {
    private static final Logger LOG = LoggerFactory.getLogger( StartResource.class );
    private final IController runner;


    @Inject
    public StartResource( IController runner, StoreService service ) {
        super( "/start", service );
        this.runner = runner;
    }


    @POST
    public Result start( @QueryParam( Constants.PARAM_PROPAGATE ) Boolean propagate ) {
        if ( runner.isRunning() ) {
            return new BaseResult( getEndpointUrl(), false, "already running", runner.getState() );
        }

        if ( runner.needsReset() ) {
            return new BaseResult( getEndpointUrl(), false, "reset needed - but save the last run data first!",
                    runner.getState() );
        }

        if ( propagate == Boolean.FALSE ) {
            runner.start();
            return new BaseResult( getEndpointUrl(), true, "successfully started", runner.getState() );
        }

        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 500L );
                }
                catch ( InterruptedException e ) {
                    LOG.error( "Awe dang someone broke my sleep!", e );
                }
                runner.start();
            }
        } ).start();

        return propagate( runner.getState(), true, "successfully started" );
    }
}
