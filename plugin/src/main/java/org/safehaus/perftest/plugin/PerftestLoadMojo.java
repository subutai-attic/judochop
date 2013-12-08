package org.safehaus.perftest.plugin;


import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo ( name = "load" )
public class PerftestLoadMojo extends PerftestMojo {

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

        Bucket bucket = null;
        for ( Bucket b : s3.listBuckets() ) {
            if ( b.getName().equals( bucketName ) ) {
                bucket = b;
                break;
            }
        }

        if ( bucket == null ) {
            throw new MojoExecutionException( bucketName + " bucket is not found with given credentials" );
        }

        String warOnS3Path = getWarOnS3Path();
        boolean warExists = false;

        for ( S3ObjectSummary file : s3.listObjects( bucketName ).getObjectSummaries() ) {
            if ( warOnS3Path.equals( file.getKey() ) ) {
                warExists = true;
                break;
            }
        }

        if ( ! warExists ) {
            // TODO instead of throwing this here, trigger perftest:deploy goal and continue if that succeeds
            throw new MojoExecutionException( "perftest.war on S3 bucket is not up to date, run perftest:war and " +
                    "perftest:deploy goals before running perftest:load" );
        }

        Result result = client.load( info, getWarOnS3Path() );

        if ( ! result.getStatus() ) {
            throw new MojoExecutionException( "Could not get the status of runners, quitting..." );
        }

        if ( ! result.getState().equals( State.READY ) ) {
            throw new MojoExecutionException( "Something went wrong while trying to load the test, runners are not " +
                    "in ready state" );
        }

        getLog().info( "Test war is loaded on each runner instance and in READY state. You can run perftest:start now" +
                " to start your tests" );


    }
}
