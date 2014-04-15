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
import org.apache.usergrid.chop.webapp.elasticsearch.ElasticSearchResource;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyResource;

import org.safehaus.jettyjam.utils.JettyUnitResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests the ChopUi.
 */
public class ChopUiTest {
    private static final Logger LOG = LoggerFactory.getLogger( ChopUiTest.class );
    private static final Map<String,String> queryParams = ChopUiTestUtils.getQueryParams();


    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = ChopUiConfig.class ) },
        filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = 8443 ) }
    )
    @ClassRule
    public static JettyResource jetty = new JettyUnitResource( ChopUiTest.class );

    @ClassRule
    public static ElasticSearchResource es = new ElasticSearchResource();


    @Test
    public void testGet() {
        String result = jetty.newTestParams()
                .setLogger( LOG )
                .setEndpoint( TestGetResource.ENDPOINT_URL )
                .newWebResource()
                .accept( MediaType.TEXT_PLAIN )
                .get( String.class );

        assertEquals( TestGetResource.TEST_MESSAGE, result );
    }


    @Test
    public void testRunManagerNext() {
        ChopUiTestUtils.testRunManagerNext( jetty.newTestParams( queryParams ).setLogger(LOG) );
    }


    @Test
    public void testRunnerRegistryList() {
        ChopUiTestUtils.testRunnerRegistryList( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryRegister() {
        ChopUiTestUtils.testRunnerRegistryRegister( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testUploadRunner() throws Exception {
        ChopUiTestUtils.testUpload( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistryUnregister() {
        ChopUiTestUtils.testRunnerRegistryUnregister( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }


    @Test
    public void testRunnerRegistrySequence() {
        ChopUiTestUtils.testRunnerRegistrySequence( jetty.newTestParams( queryParams ).setLogger( LOG ) );
    }
}
