package org.safehaus.chop.runner;


import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Test;
import org.safehaus.chop.api.Runner;
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


/**
 * An integration test for the chop UI.
 */
public class RunnerAppIT {
    private final static Logger LOG = LoggerFactory.getLogger( RunnerAppIT.class );
    private final static Properties systemProperties = new Properties();

    static {
        systemProperties.setProperty( RunnerConfig.CHOP_IT_MODE, "true" );
    }

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
    public static JettyResource jetty = new JettyIntegResource( systemProperties );


    @Test
    public void testStart() {
        RunnerTestUtils.testStart( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testReset() {
        RunnerTestUtils.testReset( jetty.newTestParams().setLogger( LOG ) );
    }
}
