package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.ProjectBuilder;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.State;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "verify" )
public class VerifyMojo extends MainMojo {

    // @todo - parallel execute this command as well
    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new ChopClientModule() );
        ChopClient client = injector.getInstance( ChopClient.class );

        // Check if the latest war is deployed on Store
        boolean result = false;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( new File( getProjectFileToUploadPath() ) ) );
            ProjectBuilder builder = new ProjectBuilder( props );
            Project currentProject = builder.getProject();
            Set<Project> tests = client.getProjectConfigs();

            for ( Project test : tests ) {
                if ( currentProject.getVcsVersion().equals( test.getVcsVersion() ) &&
                        currentProject.getWarMd5().equals( test.getWarMd5() ) ) {
                    result = true;
                    break;
                }
            }

            if ( result ) {
                getLog().info( "Test on store is up-to-date, checking runners..." );
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
