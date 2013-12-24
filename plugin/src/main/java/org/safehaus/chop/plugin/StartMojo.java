package org.safehaus.chop.plugin;


import java.io.File;
import java.util.Set;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.store.StoreOperations;
import org.safehaus.chop.api.store.amazon.AmazonStoreModule;
import org.safehaus.chop.client.PerftestClient;
import org.safehaus.chop.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo ( name = "start" )
public class StartMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        injector = Guice.createInjector( new AmazonStoreModule() );
        StoreOperations store = injector.getInstance( StoreOperations.class );

        if ( store == null ) {
            getLog().info( "Couldn't get S3 object, aborting." );
            return;
        }

        // Check if the latest war is deployed on Store
        boolean testUpToDate = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Project currentProject = mapper.readValue( new File( getProjectFileToUploadPath() ), Project.class );
            Set<Project> tests = client.getProjectConfigs();

            for ( Project test : tests ) {
                if ( currentProject.getVcsVersion().equals( test.getVcsVersion() ) &&
                        currentProject.getWarMd5().equals( test.getWarMd5() ) ) {
                    testUpToDate = true;
                    break;
                }
            }
        }
        catch ( Exception e ) {
            getLog().warn( "Error while getting test information from store", e );
        }

        Runner info = null;
        for ( Runner runner : client.getRunners() ) {
            info = runner;
            break;
        }

        if ( info == null ) {
            throw new MojoExecutionException( "There is no runner found" );
        }

        AmazonS3 s3 = Utils.getS3Client( accessKey, secretKey );
        String warOnS3Path = getWarOnS3Path();
        boolean warExists = false;

        for ( S3ObjectSummary file : s3.listObjects( bucketName ).getObjectSummaries() ) {
            if ( warOnS3Path.equals( file.getKey() ) ) {
                warExists = true;
                break;
            }
        }

        /**
         * - If latest war exists both locally and on store, try to verify the state of runners;
         *   |__ If at least one runner is in stopped state and resetIfStopped parameter of plugin is set,
         *          execute reset goal and try to verify again;
         * - If verification result in all runners with running state, set callLoadGoal to false;
         * - If callLoadGoal is true, execute load goal in chain;
         */
        boolean callLoadGoal = true;
        if( warExists && testUpToDate ) {
            Result verifyResult = client.verify();
            if( resetIfStopped && verifyResult.getStatus() && verifyResult.getState().equals( State.STOPPED ) ) {
                getLog().info( "There is at least one runner in STOPPED state, calling reset goal..." );
                ResetMojo resetMojo = new ResetMojo( this );
                resetMojo.execute();
                verifyResult = client.verify();
            }
            callLoadGoal = ( verifyResult.getStatus() && verifyResult.getState().equals( State.RUNNING ) );
        }
        if ( callLoadGoal ) {
            getLog().info( "Cluster is not ready to start the tests, calling perftest:load goal..."  );
            LoadMojo loadMojo = new LoadMojo( this );
            loadMojo.execute();
        }

        Result result = client.start( info, true );

        if ( ! result.getStatus() ) {
            throw new MojoExecutionException( result.getMessage() );
        }

        getLog().info( "Start request resulted with: " + result.getMessage() );

    }
}