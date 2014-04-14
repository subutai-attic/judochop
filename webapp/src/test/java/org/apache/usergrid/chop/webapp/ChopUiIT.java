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

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.WebResource;
import org.junit.ClassRule;
import org.junit.Test;
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

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import org.apache.usergrid.chop.webapp.coordinator.rest.AuthResource;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;

/**
 * An integration test for the chop UI.
 */
public class ChopUiIT {

    private final static Logger LOG = LoggerFactory.getLogger( ChopUiIT.class );

    private final static String[] ARGS = new String[]{ "-e" };

    private final static Map<String, String> QUERY_PARAMS = ChopUiTestUtils.getQueryParams();

    private final static String SHIRO_AUTHENTICATION_EXCEPTION = "org.apache.shiro.authc.AuthenticationException";
    private final static String SHIRO_UNAUTHORIZED_EXCEPTION = "org.apache.shiro.authz.UnauthorizedException";

    private final static Map<String, String> WRONG_USER_PARAMS = new HashMap<String, String>() {
        {
            put("user", "user");
            put("pwd", "foo");
        }
    };

    private ByteArrayOutputStream outContent;

    @JettyContext(
            enableSession = true,
            contextListeners = { @ContextListener(listener = ChopUiConfig.class) },
            filterMappings = { @FilterMapping(filter = GuiceFilter.class, spec = "/*") }
    )

    @JettyConnectors(
            defaultId = "https",
            httpsConnectors = { @HttpsConnector(id = "https", port = 8443) }
    )

    @ClassRule
    public static JettyResource jetty = new JettyIntegResource(ARGS);

    @Before
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut( new PrintStream(outContent) );
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }


    @Test
    public void testRunManagerNext() {
        ChopUiTestUtils.testRunManagerNext( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
    }

    @Test
    public void testRunnerRegistryList() {
        ChopUiTestUtils.testRunnerRegistryList( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
    }

    @Test
    public void testRunnerRegistryRegister() {
        ChopUiTestUtils.testRunnerRegistryRegister( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
    }

    @Test
    public void testUploadRunner() throws Exception {
        ChopUiTestUtils.testUpload( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
    }

    @Test
    public void testRunnerRegistryUnregister() {
        ChopUiTestUtils.testRunnerRegistryUnregister( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
    }

    @Test
    public void testRunnerRegistrySequence() {
        ChopUiTestUtils.testRunnerRegistrySequence( jetty.newTestParams(QUERY_PARAMS).setLogger(LOG) );
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


    private static WebResource.Builder getWebResourceBuilder(Map<String, String> params, String endpoint) {
        return jetty.newTestParams(params)
                .setEndpoint(endpoint)
                .newWebResource()
                .accept(MediaType.APPLICATION_JSON);
    }


    @Test
    public void testAuthGet() {
        String result = getWebResourceBuilder(QUERY_PARAMS, AuthResource.ENDPOINT_URL).get(String.class);

        assertEquals(AuthResource.GET_MESSAGE, result);
    }


    @Test
    public void testAuthPost() {
        String result = getWebResourceBuilder(QUERY_PARAMS, AuthResource.ENDPOINT_URL).post(String.class);

        assertEquals( AuthResource.POST_MESSAGE, result);
    }


    @Test
    public void testAuthGetWithWrongCredentials() {

        boolean exceptionThrown = false;

        try {
            getWebResourceBuilder(WRONG_USER_PARAMS, AuthResource.ENDPOINT_URL).get(String.class);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue( "No AuthenticationException thrown", exceptionThrown );
        assertTrue(outContent.toString().contains(SHIRO_AUTHENTICATION_EXCEPTION));
    }


    @Test
    public void testAuthPostWithWrongCredentials() {
        boolean exceptionThrown = false;

        try {
            getWebResourceBuilder(WRONG_USER_PARAMS, AuthResource.ENDPOINT_URL).post(String.class);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue( "No AuthenticationException thrown", exceptionThrown );
        assertTrue( outContent.toString().contains(SHIRO_AUTHENTICATION_EXCEPTION) );
    }


    @Test
    public void testAuthPostWithAllowedRole() {
        String result = getWebResourceBuilder( QUERY_PARAMS, AuthResource.ENDPOINT_URL + AuthResource.ALLOWED_ROLE_PATH ).post(String.class);

        assertEquals( AuthResource.POST_WITH_ALLOWED_ROLE_MESSAGE, result);
    }


    @Test
    public void testAuthPostWithUnallowedRole() {
        boolean exceptionThrown = false;

        try {
            getWebResourceBuilder(QUERY_PARAMS, AuthResource.ENDPOINT_URL + AuthResource.UNALLOWED_ROLE_PATH).post(String.class);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue( "No AuthenticationException thrown", exceptionThrown );
        assertTrue( outContent.toString().contains(SHIRO_UNAUTHORIZED_EXCEPTION) );
    }


    @Test
    public void testAuthGetWithAllowedRole() {
        String result = getWebResourceBuilder( QUERY_PARAMS, AuthResource.ENDPOINT_URL + AuthResource.ALLOWED_ROLE_PATH ).get(String.class);

        assertEquals( AuthResource.GET_WITH_ALLOWED_ROLE_MESSAGE, result);
    }


    @Test
    public void testAuthGetWithUnallowedRole() {
        boolean exceptionThrown = false;

        try {
            getWebResourceBuilder(QUERY_PARAMS, AuthResource.ENDPOINT_URL + AuthResource.UNALLOWED_ROLE_PATH).get(String.class);
        } catch (Exception e) {
            exceptionThrown = true;
        }

        assertTrue( "No AuthenticationException thrown", exceptionThrown );
        assertTrue( outContent.toString().contains(SHIRO_UNAUTHORIZED_EXCEPTION) );
    }

}
