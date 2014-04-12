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
package org.apache.usergrid.chop.integ;


import org.junit.ClassRule;
import org.junit.Test;
import org.apache.usergrid.chop.runner.RunnerConfig;
import org.apache.usergrid.chop.webapp.ChopUiConfig;
import org.apache.usergrid.chop.webapp.elasticsearch.ElasticSearchResource;
import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyResource;
import org.safehaus.jettyjam.utils.JettyUnitResource;
import org.safehaus.jettyjam.utils.StartResources;

import com.google.inject.servlet.MultiAppGuiceFilter;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Unit tests the various interactions between runners and the coordinator.
 *
 * This unit test starts up the chop web UI as a jetty jam unit resource
 * and then proceeds to start up two runners generated from the example project
 * using chop:runner.
 */
public class RunnerCoordinatorTest {


    @ClassRule
    public static ElasticSearchResource esResource = new ElasticSearchResource();


    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = ChopUiConfig.class ) },
        filterMappings = { @FilterMapping( filter = MultiAppGuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = 8443 ) }
    )
    public static JettyResource webapp = new JettyUnitResource( RunnerCoordinatorTest.class );

    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = RunnerConfig.class ) },
        filterMappings = { @FilterMapping( filter = MultiAppGuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = 0 ) }
    )
    public static JettyResource runner1 = new JettyUnitResource( RunnerCoordinatorTest.class );

    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = RunnerConfig.class ) },
        filterMappings = { @FilterMapping( filter = MultiAppGuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = 0 ) }
    )
    public static JettyResource runner2 = new JettyUnitResource( RunnerCoordinatorTest.class );


    @ClassRule
    public static StartResources resources = new StartResources( 1000, esResource, webapp, runner1, runner2 );


    @Test
    public void testBasic() {
        assertNotNull( esResource );
        assertTrue( webapp.isStarted() );
        assertTrue( runner1.isStarted() );
        assertTrue( runner2.isStarted() );
    }
}
