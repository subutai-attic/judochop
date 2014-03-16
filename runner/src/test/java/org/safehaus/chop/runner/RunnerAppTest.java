package org.safehaus.chop.runner;


import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.api.Runner;
import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.JettyResource;

import com.google.inject.servlet.GuiceFilter;


/**
 * Tests the Runner.
 */
public class RunnerAppTest {

    @JettyContext(
        enableSession = true,
        contextListeners = { @ContextListener( listener = RunnerConfig.class ) },
        filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
    )
    @JettyConnectors(
        defaultId = "https",
        httpsConnectors = { @HttpsConnector( id = "https", port = Runner.DEFAULT_SERVER_PORT_INT ) }
    )
    @ClassRule
    public static JettyResource jetty = new JettyResource();


    @Test
    public void testRunner() {

    }
}
