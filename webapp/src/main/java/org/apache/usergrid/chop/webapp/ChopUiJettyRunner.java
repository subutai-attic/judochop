package org.apache.usergrid.chop.webapp;


import org.safehaus.jettyjam.utils.ContextListener;
import org.safehaus.jettyjam.utils.FilterMapping;
import org.safehaus.jettyjam.utils.HttpsConnector;
import org.safehaus.jettyjam.utils.JettyConnectors;
import org.safehaus.jettyjam.utils.JettyContext;
import org.safehaus.jettyjam.utils.JettyRunner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
public class ChopUiJettyRunner extends JettyRunner {

    private static CommandLine cl;


    public ChopUiJettyRunner() {
        super( ChopUiJettyRunner.class.getSimpleName() );
    }


    @Override
    public String getSubClass() {
        return getClass().getName();
    }


    public static void main( String[] args ) throws Exception {
        processCli( args );
        ChopUiJettyRunner launcher = new ChopUiJettyRunner();
        launcher.start();
    }


    public static CommandLine getCommandLine() {
        return cl;
    }


    static void processCli( String[] args ) {
        CommandLineParser parser = new PosixParser();
        Options options = getOptions();

        try {
            cl = parser.parse( options, args );
        }
        catch ( ParseException e ) {
            if ( e instanceof MissingArgumentException ) {
                System.out.println( "Missing option: " + ( ( MissingArgumentException ) e ).getOption() );
            }

            help( options );
            System.exit( 1 );
        }

        if ( cl.hasOption( 'h' ) ) {
            help( options );
            System.exit( 0 );
        }
    }


    static void help( Options options ) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ChopUi", options );
    }


    static Options getOptions() {
        Options options = new Options();

        options.addOption( "h", "help", false, "Print out help." );
        options.addOption( "e", "embedded", false, "Starts an embedded ES instance." );
        options.addOption( "d", "home-dir", true, "The home directory for ChopUi: path to " +
                "home directory argument." );
        options.addOption( "j", "join", true, "Joins an existing ES cluster: cluster name argument." );
        options.addOption( "c", "client-only", true, "Client to existing ES cluster: transport address argument " +
                "(i.e. localhost:3456)" );

        return options;
    }
}
