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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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


    @POST
    @Path( "/list" )
    public Response list() throws Exception {
        LOG.warn( "Calling list ..." );

        Map<String, Runner> runnerMap = runnerDao.getRunners();
        List<Runner> runnerList = new ArrayList<Runner>( runnerMap.size() );
        Runner[] runners = new Runner[runnerMap.size()];

        for ( String key : runnerMap.keySet() ) {
            runnerList.add( runnerMap.get( key ) );
        }

        return Response.status( Response.Status.CREATED )
                       .entity( runnerList.toArray( runners ) ).build();
    }


    @POST
    @Path( "/register" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response register() {
        LOG.warn( "Calling register ..." );



        return Response.status( Response.Status.CREATED )
                       .entity( "TRUE" ).build();
    }


    @POST
    @Path( "/unregister" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response unregister() {
        LOG.warn( "Calling unregister ..." );
        return Response.status( Response.Status.CREATED )
                       .entity( "TRUE" ).build();
    }
}
