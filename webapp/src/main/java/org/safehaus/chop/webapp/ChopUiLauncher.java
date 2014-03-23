package org.safehaus.chop.webapp;


import org.safehaus.embedded.jetty.utils.ContextListener;
import org.safehaus.embedded.jetty.utils.FilterMapping;
import org.safehaus.embedded.jetty.utils.HttpsConnector;
import org.safehaus.embedded.jetty.utils.JettyConnectors;
import org.safehaus.embedded.jetty.utils.JettyContext;
import org.safehaus.embedded.jetty.utils.Launcher;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

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

    private static CommandLine cl;


    public ChopUiLauncher() {
        super( ChopUiLauncher.class.getSimpleName(), ChopUiLauncher.class.getClassLoader() );
    }


    @Override
    public String getPackageBase() {
        return getClass().getPackage().getName();
    }


    public static void main( String[] args ) throws Exception {
        processCli( args );
        ChopUiLauncher launcher = new ChopUiLauncher();
        launcher.start();
    }


    public static CommandLine getCommandLine() {
        return cl;
    }


    static void processCli( String[] args ) throws Exception {
        CommandLineParser parser = new PosixParser();
        cl = parser.parse( getOptions(), args );
    }


    static Options getOptions() {
        Options options = new Options();

        options.addOption( "e", "embedded", false, "Starts an embedded ES instance" );
        options.addOption( "j", "join", true, "Joins an existing ES cluster: cluster name argument." );
        options.addOption( "c", "client-only", true, "Client to existing ES cluster: transport address" );

        return options;
    }
}
