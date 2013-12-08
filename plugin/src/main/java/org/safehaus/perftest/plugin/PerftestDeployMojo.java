package org.safehaus.perftest.plugin;


import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;


/**
 * Deploys the perftest.war created by war goal to S3 bucket using supplied configuration parameters
 */
@Mojo( name = "deploy" )
public class PerftestDeployMojo extends PerftestMojo {

    @Override
    public void execute() throws MojoExecutionException {
        String sourceFile = getWarToUploadPath();
        String destinationFile = getWarOnS3Path();
        File source = new File( sourceFile );
        if ( !source.exists() ) {
            // TODO call 'war' goal instead of throwing exception, then continue
            throw new MojoExecutionException( "File doesn't exist: " + sourceFile );
        }

        AmazonS3 s3 = PerftestUtils.getS3Client( accessKey, secretKey );

        if ( !s3.doesBucketExist( bucketName ) ) {
            throw new MojoExecutionException( "Bucket doesn't exist: " + bucketName );
        }

        boolean success = PerftestUtils.uploadToS3( s3, bucketName, destinationFile, source );
        if ( !success ) {
            throw new MojoExecutionException( "Unable to upload file to S3." );
        }

        getLog().info( "File " + source + " uploaded to s3://" + bucketName + "/" + destinationFile );
    }

}
