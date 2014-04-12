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

import com.google.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import org.apache.shiro.authz.annotation.RequiresRoles;

/**
 * REST operation for authentication
 */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path(AuthResource.ENDPOINT_URL)
public class AuthResource {

    public final static String GET_MESSAGE = "/auth GET called with right credentials";
    public final static String POST_MESSAGE = "/auth POST called with right credentials";
    public final static String POST_WITH_ALLOWED_ROLE_MESSAGE = "/auth POST with allowed role called";
    public final static String POST_WITH_UNALLOWED_ROLE_MESSAGE = "/auth POST with unallowed role called";
    public final static String GET_WITH_ALLOWED_ROLE_MESSAGE = "/auth GET with allowed role called";
    public final static String GET_WITH_UNALLOWED_ROLE_MESSAGE = "/auth GET with unallowed role called";
    public final static String ENDPOINT_URL = "/auth";
    public final static String ALLOWED_ROLE_PATH = "/allowed";
    public final static String UNALLOWED_ROLE_PATH = "/unallowed";
    private static final Logger LOG = LoggerFactory.getLogger(AuthResource.class);

    @Inject
    public AuthResource() {
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    public String testget() {
        LOG.info("Calling auth via GET");
        return GET_MESSAGE;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public String testpost() {
        LOG.info("Calling auth via POST");
        return POST_MESSAGE;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(ALLOWED_ROLE_PATH)
    @RequiresRoles("role1")
    public String testpostwithrole() {
        LOG.info("Calling auth via POST with ALLOWED role");
        return POST_WITH_ALLOWED_ROLE_MESSAGE;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(UNALLOWED_ROLE_PATH)
    @RequiresRoles("role2")
    public String testpostwithunallowedrole() {
        LOG.info("Calling auth via POST with unallowed role");
        return POST_WITH_UNALLOWED_ROLE_MESSAGE;
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(ALLOWED_ROLE_PATH)
    @RequiresRoles("role1")
    public String testGetWithAllowedRole() {
        LOG.info("Calling auth via GET with allowed role");
        return GET_WITH_ALLOWED_ROLE_MESSAGE;
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(UNALLOWED_ROLE_PATH)
    @RequiresRoles("role2")
    public String testGetWithUnallowedRole() {
        LOG.info("Calling auth via GET with unallowed role");
        return GET_WITH_UNALLOWED_ROLE_MESSAGE;
    }
}
