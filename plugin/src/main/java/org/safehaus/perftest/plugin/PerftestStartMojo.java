package org.safehaus.perftest.plugin;


import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo ( name = "start" )
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

        AmazonS3 s3 = PerftestUtils.getS3Client( accessKey, secretKey );
        String warOnS3Path = getWarOnS3Path();
        boolean warExists = false;

        for ( S3ObjectSummary file : s3.listObjects( bucketName ).getObjectSummaries() ) {
            if ( warOnS3Path.equals( file.getKey() ) ) {
                warExists = true;
                break;
            }
        }

        if ( ! warExists || ! client.verify() ) {
            getLog().info( "Cluster is not ready to start the tests, calling perftest:load goal..."  );
            PerftestLoadMojo loadMojo = new PerftestLoadMojo( this );
            loadMojo.execute();
        }

        Result result = client.start( info, true );

        if ( ! result.getStatus() ) {
            throw new MojoExecutionException( result.getMessage() );
        }

        getLog().info( "Start request resulted with: " + result.getMessage() );

    }
}