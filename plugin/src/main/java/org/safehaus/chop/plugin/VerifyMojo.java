package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.ProjectFigBuilder;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.State;
import org.safehaus.chop.client.PerftestClient;
import org.safehaus.chop.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "verify" )
public class VerifyMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        // Check if the latest war is deployed on Store
        boolean result = false;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( new File( getProjectFileToUploadPath() ) ) );
            ProjectFigBuilder builder = new ProjectFigBuilder( props );
            ProjectFig currentProject = builder.getProject();
            Set<ProjectFig> tests = client.getProjectConfigs();

            for ( ProjectFig test : tests ) {
                if ( currentProject.getVcsVersion().equals( test.getVcsVersion() ) &&
                        currentProject.getWarMd5().equals( test.getWarMd5() ) ) {
                    result = true;
                    break;
                }
            }

            if ( result ) {
                getLog().info( "Test on store is up-to-date, checking drivers..." );
                Result verifyResult = client.verify();
                result = ( verifyResult.getStatus() && verifyResult.getState().equals( State.READY ) );
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
