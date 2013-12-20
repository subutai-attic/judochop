package org.safehaus.perftest.plugin;


import java.io.File;
import java.util.Set;

import org.safehaus.chop.api.TestInfo;
import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "verify" )
public class PerftestVerifyMojo extends PerftestMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        // Check if the latest war is deployed on Store
        boolean result = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            TestInfo currentTestInfo = mapper.readValue( new File( getTestInfoToUploadPath() ), TestInfo.class );
            Set<TestInfo> tests = client.getTests();

            for ( TestInfo test : tests ) {
                if ( currentTestInfo.getGitUuid().equals( test.getGitUuid() ) &&
                        currentTestInfo.getWarMd5().equals( test.getWarMd5() ) ) {
                    result = true;
                    break;
                }
            }

            if ( result ) {
                getLog().info( "Test on store is up-to-date, checking runners..." );
                result &= client.verify();
            }
            else {
                getLog().info( "Test on Store is not up-to-date" );
            }

        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Verify goal failed", e );
        }

        if ( result ) {
            getLog().info( "Verification succeeded, tests are ready to start on the cluster" );
        }
        else {
            throw new MojoExecutionException( "Cluster is not ready to start the tests" );
        }

    }
}
