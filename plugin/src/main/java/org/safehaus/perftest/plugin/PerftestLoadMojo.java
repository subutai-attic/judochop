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


@Mojo(name = "load")
public class PerftestLoadMojo extends PerftestMojo {


    protected PerftestLoadMojo( PerftestMojo mojo ) {
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.accessKey = mojo.accessKey;
        this.secretKey = mojo.secretKey;
        this.bucketName = mojo.bucketName;
        this.destinationParentDir = mojo.destinationParentDir;
        this.managerAppUsername = mojo.managerAppUsername;
        this.managerAppPassword = mojo.managerAppPassword;
        this.testModuleFQCN = mojo.testModuleFQCN;
        this.perftestFormation = mojo.perftestFormation;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    protected PerftestLoadMojo() {

    }


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

        if ( !warExists ) {
            getLog().info( "War on store is not up-to-date, calling perftest:deploy goal now..." );
            PerftestDeployMojo deployMojo = new PerftestDeployMojo( this );
            deployMojo.execute();
        }

        Result result = client.load( info, getWarOnS3Path(), true );

        if ( !result.getStatus() ) {
            throw new MojoExecutionException( "Could not get the status of runners, quitting..." );
        }

        if ( !result.getState().equals( State.READY ) ) {
            throw new MojoExecutionException(
                    "Something went wrong while trying to load the test, runners are not " + "in ready state" );
        }

        getLog().info( "Test war is loaded on each runner instance and in READY state. You can run perftest:start now"
                + " to start your tests" );
    }
}
