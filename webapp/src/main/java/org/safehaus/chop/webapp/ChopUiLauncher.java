package org.safehaus.chop.webapp;


import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.Launcher;

import com.google.inject.servlet.GuiceFilter;


/**
 * The executable jar file main entry point is contained in this launcher class which
 * fires up an embedded jetty instance based on jettyjam configuration annotations.
 */
@JettyContext(
    enableSession = true,
    contextListeners = { @ContextListener( listener = ChopUiConfig.class ) },
    filterMappings = { @FilterMapping( filter = GuiceFilter.class, spec = "/*") }
)
@JettyConnectors(
    defaultId = "https",
    httpsConnectors = { @HttpsConnector( id = "https", port = 8443 ) }
)
public class ChopUiLauncher extends Launcher {

    public ChopUiLauncher() {
        super( ChopUiLauncher.class.getSimpleName(), ChopUiLauncher.class.getClassLoader() );
    }


    @Override
    public String getPackageBase() {
        return getClass().getPackage().getName();
    }


    public static void main( String[] args ) throws Exception {
        ChopUiLauncher launcher = new ChopUiLauncher();
        launcher.start();
    }
}
