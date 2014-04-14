/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;
import org.apache.usergrid.chop.api.RestParams;
import org.apache.usergrid.chop.webapp.coordinator.rest.TestGetResource;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyIntegResource;

import org.safehaus.jettyjam.utils.JettyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import org.apache.usergrid.chop.webapp.coordinator.rest.AuthResource;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.After;
import org.junit.Before;

/**
 * An integration test for the chop UI.
 */
public class ChopUiIT {

    private final static Logger LOG = LoggerFactory.getLogger(ChopUiIT.class);
    private final static Map<String, String> queryParams = new HashMap<String, String>();
    private final static String[] args = new String[]{"-e"};

    static {
        queryParams.put(RestParams.PASSWORD, "pass");
        queryParams.put(RestParams.USERNAME, "user");
        queryParams.put(RestParams.COMMIT_ID, UUID.randomUUID().toString());
        queryParams.put(RestParams.MODULE_VERSION, "2.0.0-SNAPSHOT");
        queryParams.put(RestParams.MODULE_ARTIFACTID, "chop-example");
        queryParams.put(RestParams.MODULE_GROUPID, "org.apache.usergrid.chop");
        queryParams.put(RestParams.TEST_PACKAGE, "org.apache.usergrid.chop.example");
    }

    @JettyContext(
            enableSession = true,
            contextListeners = {
                @ContextListener(listener = ChopUiConfig.class)},
            filterMappings = {
                @FilterMapping(filter = GuiceFilter.class, spec = "/*")}
    )
    @JettyConnectors(
            defaultId = "https",
            httpsConnectors = {
                @HttpsConnector(id = "https", port = 8443)}
    )
    @ClassRule
    public static JettyResource jetty = new JettyIntegResource(args);
    private ByteArrayOutputStream outContent;

    @Before
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void testGet() {
        String result = jetty.newTestParams()
                .setEndpoint(TestGetResource.ENDPOINT_URL)
                .newWebResource()
                .accept(MediaType.TEXT_PLAIN)
                .get(String.class);
        assertEquals(TestGetResource.TEST_MESSAGE, result);
    }

    @Test
    public void testAuthGet() {
        String result = jetty.newTestParams(queryParams)
                .setEndpoint(AuthResource.ENDPOINT_URL)
                .newWebResource()
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        assertEquals(AuthResource.GET_MESSAGE, result);
    }

    @Test
    public void testAuthGetWithWrongCredentials() {
        try {
            jetty.newTestParams(new HashMap() {
                {
                    put("user", "user");
                    put("pwd", "foo");
                }
            })
                    .setEndpoint(AuthResource.ENDPOINT_URL)
                    .newWebResource()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
        } catch (Exception e) {
        }
        assertThat(outContent.toString(), containsString("org.apache.shiro.authc.AuthenticationException: Authentication failed"));

    }

    @Test
    public void testAuthPost() {
        String result = jetty.newTestParams(queryParams)
                .setEndpoint(AuthResource.ENDPOINT_URL)
                .newWebResource()
                .accept(MediaType.APPLICATION_JSON)
                .post(String.class);
        assertEquals(AuthResource.POST_MESSAGE, result);
    }

    @Test
    public void testAuthPostWithWrongCredentials() {
        try {
            jetty.newTestParams(new HashMap() {
                {
                    put("user", "user");
                    put("pwd", "foo");
                }
            })
                    .setEndpoint(AuthResource.ENDPOINT_URL)
                    .newWebResource()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(String.class);
        } catch (Exception e) {
        }
        assertThat(outContent.toString(), containsString("org.apache.shiro.authc.AuthenticationException: Authentication failed"));

    }

    @Test
    public void testAuthPostWithAllowedRole() {
        String result = jetty.newTestParams(queryParams)
                .setEndpoint(AuthResource.ENDPOINT_URL + AuthResource.ALLOWED_ROLE_PATH)
                .newWebResource()
                .accept(MediaType.APPLICATION_JSON)
                .post(String.class);
        assertEquals(AuthResource.POST_WITH_ALLOWED_ROLE_MESSAGE, result);
    }

    @Test
    public void testAuthPostWithUnallowedRole() {
        try {
            jetty.newTestParams(queryParams)
                    .setEndpoint(AuthResource.ENDPOINT_URL + AuthResource.UNALLOWED_ROLE_PATH)
                    .newWebResource()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(String.class);
        } catch (Exception e) {
        }
        assertThat(outContent.toString(), containsString("org.apache.shiro.authz.UnauthorizedException: Subject does not have role"));
    }

    @Test
    public void testAuthGetWithAllowedRole() {
        String result = jetty.newTestParams(queryParams)
                .setEndpoint(AuthResource.ENDPOINT_URL + AuthResource.ALLOWED_ROLE_PATH)
                .newWebResource()
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);
        assertEquals(AuthResource.GET_WITH_ALLOWED_ROLE_MESSAGE, result);
    }

    @Test
    public void testAuthGetWithUnallowedRole() {
        try {
            jetty.newTestParams(queryParams)
                    .setEndpoint(AuthResource.ENDPOINT_URL + AuthResource.UNALLOWED_ROLE_PATH)
                    .newWebResource()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(String.class);
        } catch (Exception e) {
        }
        assertThat(outContent.toString(), containsString("org.apache.shiro.authz.UnauthorizedException: Subject does not have role"));
    }

    @Test
    public void testRunManagerNext() {
        ChopUiTestUtils.testRunManagerNext(jetty.newTestParams(queryParams).setLogger(LOG));
    }

    @Test
    public void testRunnerRegistryList() {
        ChopUiTestUtils.testRunnerRegistryList(jetty.newTestParams(queryParams).setLogger(LOG));
    }

    @Test
    public void testRunnerRegistryRegister() {
        ChopUiTestUtils.testRunnerRegistryRegister(jetty.newTestParams(queryParams).setLogger(LOG));
    }

    @Test
    public void testUploadRunner() throws Exception {
        ChopUiTestUtils.testUpload(jetty.newTestParams(queryParams).setLogger(LOG));
    }

    @Test
    public void testRunnerRegistryUnregister() {
        ChopUiTestUtils.testRunnerRegistryUnregister(jetty.newTestParams(queryParams).setLogger(LOG));
    }

    @Test
    public void testRunnerRegistrySequence() {
        ChopUiTestUtils.testRunnerRegistrySequence(jetty.newTestParams(queryParams).setLogger(LOG));
    }
}
