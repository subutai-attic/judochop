package org.safehaus.chop.plugin;


import java.io.File;
import java.util.Set;

import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.StoreService;
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
        StoreService service = injector.getInstance( StoreService.class );

        if ( service == null ) {
            getLog().info( "Couldn't get S3 object, aborting." );
            return;
        }

        // Check if the latest war is deployed on Store
        boolean testUpToDate = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ProjectFig currentProject = mapper.readValue( new File( getProjectFileToUploadPath() ), ProjectFig.class );
            Set<ProjectFig> tests = client.getProjectConfigs();

            for ( ProjectFig test : tests ) {
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

        RunnerFig info = null;
        for ( RunnerFig runnerFig : client.getRunners() ) {
            info = runnerFig;
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

        if ( ! warExists || ! testUpToDate || ! client.verify() ) {
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