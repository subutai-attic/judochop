package org.safehaus.chop.runner;


import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyRunner;

import com.google.inject.servlet.GuiceFilter;


/**
 * Launches the Runner web application and has the main() entry point for the executable
 * jar file for the Runner.
 */
@JettyContext(
    enableSession = true,
    contextListeners = { @ContextListener( listener = RunnerConfig.class ) },
    filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
)
@JettyConnectors(
    defaultId = "https",
    httpsConnectors = { @HttpsConnector( id = "https", port = 0 ) }
)
public class RunnerAppJettyRunner extends JettyRunner {

    protected RunnerAppJettyRunner() {
        super( RunnerAppJettyRunner.class.getSimpleName() );
    }


    @Override
    public String getSubClass() {
        return getClass().getName();
    }


    public static void main( String[] args ) throws Exception {
        RunnerAppJettyRunner launcher = new RunnerAppJettyRunner();
        launcher.start();
    }
}
