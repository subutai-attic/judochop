package org.safehaus.perftest.plugin;


import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "verify" )
public class PerftestVerifyMojo extends PerftestMojo {

    @Override
    public void execute() throws MojoExecutionException {
        boolean result;
        try {

            Injector injector = Guice.createInjector( new PerftestClientModule() );
            PerftestClient client = injector.getInstance( PerftestClient.class );

            result = client.verify();

        } catch ( Exception e ) {
            throw new MojoExecutionException( "Verify goal failed", e );
        }

        if ( result ) {
            getLog().info( "Verification succeeded, tests are ready to start on the cluster" );
        } else {
            throw new MojoExecutionException( "Cluster is not ready to start the tests" );
        }

    }
}
