package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.ProjectFigBuilder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/** Deploys the perftest.war created by war goal to S3 bucket using supplied configuration parameters */
@Mojo(name = "deploy" )
public class DeployMojo extends MainMojo {


    protected DeployMojo( MainMojo mojo ) {
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.accessKey = mojo.accessKey;
        this.secretKey = mojo.secretKey;
        this.bucketName = mojo.bucketName;
        this.destinationParentDir = mojo.destinationParentDir;
        this.managerAppUsername = mojo.managerAppUsername;
        this.managerAppPassword = mojo.managerAppPassword;
        this.testPackageBase = mojo.testPackageBase;
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.amiID = mojo.amiID;
        this.awsSecurityGroup = mojo.awsSecurityGroup;
        this.runnerKeyPairName = mojo.runnerKeyPairName;
        this.runnerName = mojo.runnerName;
        this.instanceType = mojo.instanceType;
        this.setupTimeout = mojo.setupTimeout;
        this.minimumRunners = mojo.minimumRunners;
        this.maximumRunners = mojo.maximumRunners;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }

    protected DeployMojo() {

    }

    @Override
    public void execute() throws MojoExecutionException {
        String sourceFile = getWarToUploadPath();
        String destinationFile = getWarOnS3Path();
        String projectFileKey = getProjectFilePath();
        File source = new File( sourceFile );
        File projectFile = new File( source.getParent(), PROJECT_FILE );

        if ( ! isReadyToDeploy() ) {
            getLog().info( "War is not ready to upload to store, calling perftest:war goal now..." );
            WarMojo warMojo = new WarMojo( this );
            warMojo.execute();
        }

        if ( ! isReadyToDeploy() ) {
            throw new MojoExecutionException( "Files to be deployed are not ready and chop:war failed" );
        }

        try {
            // Write deploy time to loadTime property in project.json
            ObjectMapper mapper = new ObjectMapper();
            ProjectFigBuilder proj = new ProjectFigBuilder( mapper.readValue( projectFile, ProjectFig.class ) );
            proj.setLoadTime( Utils.getTimestamp( new Date() ) );
            mapper.writeValue( projectFile, proj );
        }
        catch ( JsonMappingException e ) {
            throw new MojoExecutionException( "Project.json file format is faulty", e );
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Error while modifying project.json file", e );
        }


        AmazonS3 s3 = Utils.getS3Client( accessKey, secretKey );

        if ( !s3.doesBucketExist( bucketName ) ) {
            throw new MojoExecutionException( "Bucket doesn't exist: " + bucketName );
        }

        getLog().info( "Uploading file to: " + destinationFile );

        boolean success = Utils.uploadToS3( s3, bucketName, destinationFile, source );
        if ( !success ) {
            throw new MojoExecutionException( "Unable to upload war file to S3." );
        }

        success = Utils.uploadToS3( s3, bucketName, projectFileKey, projectFile );
        if ( !success ) {
            throw new MojoExecutionException( "Unable to upload $PROJECT_FILE file to S3." );
        }

        getLog().info( "File " + source + " uploaded to s3://" + bucketName + "/" + destinationFile );
    }


    private boolean isReadyToDeploy() {
        File source = new File( getWarToUploadPath() );
        try {
            if ( ! source.exists() ) {
                return false;
            }

            File extractedConfigPropFile = new File( getExtractedWarRootPath() + "WEB-INF/classes/config.properties" );
            if ( extractedConfigPropFile.exists() ) {
                Properties props = new Properties();
                FileInputStream inputStream = new FileInputStream( extractedConfigPropFile );
                props.load( inputStream );
                inputStream.close();

                String commitId = Utils.getLastCommitUuid( Utils.getGitConfigFolder( getProjectBaseDirectory() ) );
                return commitId.equals( props.getProperty( ProjectFig.GIT_UUID_KEY ) );
            }
        } catch ( Exception e ) {
            getLog().warn( e );
        }
        return false;
    }
}
