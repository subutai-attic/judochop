package org.safehaus.perftest.plugin;



import java.util.Collection;

import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo(name = "start")
public class PerftestStartMojo extends PerftestMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        RunnerInfo info = null;
        for ( RunnerInfo runner : client.getRunners() ) {
            info = runner;
            break;
        }

        if ( info == null ) {
            throw new MojoExecutionException( "There is no runner found" );
        }

        Result result = client.start( info, true );

        if ( ! result.getStatus() ) {
            throw new MojoExecutionException( result.getMessage() );
        }

        getLog().info( "Start request resulted with: " + result.getMessage() );

    }
}
