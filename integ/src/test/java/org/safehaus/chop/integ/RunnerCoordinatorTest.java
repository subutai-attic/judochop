package org.safehaus.chop.integ;


import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.runner.RunnerConfig;
import org.safehaus.chop.webapp.ChopUiConfig;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchResource;
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
