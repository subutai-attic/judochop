package org.safehaus.chop.runner;


import org.safehaus.chop.api.Runner;
import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.Launcher;

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
    httpsConnectors = { @HttpsConnector( id = "https", port = Runner.DEFAULT_SERVER_PORT_INT ) }
)
public class RunnerLauncher extends Launcher {

    protected RunnerLauncher() {
        super( RunnerLauncher.class.getSimpleName(), RunnerLauncher.class.getClassLoader() );
    }


    @Override
    public String getPackageBase() {
        return getClass().getPackage().getName();
    }


    public static void main( String[] args ) throws Exception {
        RunnerLauncher launcher = new RunnerLauncher();
        launcher.start();
    }
}
