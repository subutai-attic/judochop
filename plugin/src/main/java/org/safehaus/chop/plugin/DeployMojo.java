package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.safehaus.chop.api.Project;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


/** Deploys the jar created by jar goal to coordinator using supplied configuration parameters */
@Mojo(name = "deploy", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class DeployMojo extends MainMojo {


    protected DeployMojo( MainMojo mojo ) {
        this.username = mojo.username;
        this.password = mojo.password;
        this.endpoint = mojo.endpoint;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.runnerCount = mojo.runnerCount;
    }


    @Override
    public void execute() throws MojoExecutionException {
        File buildDir = new File( project.getBuild().getDirectory() );
        getLog().info( "buildDir = " + buildDir.getAbsolutePath() );

        File source = new File( buildDir, RUNNER_JAR );
        if ( source.exists() ) {
            getLog().info( source.getAbsolutePath() + " exists!" );
        }
        else {
            getLog().info( source.getAbsolutePath() + " does not exist." );
        }


        if ( ! isReadyToDeploy() ) {
            getLog().info( RUNNER_JAR + " is not ready to upload to store, calling chop:runner goal now..." );
            RunnerMojo runnerMojo = new RunnerMojo( this );
            runnerMojo.execute();
        }

        if ( ! isReadyToDeploy() ) {
            throw new MojoExecutionException( "Files to be deployed are not ready and chop:runner failed" );
        }

//        String configPropertiesFilePath = getExtractedWarRootPath() + "WEB-INF/classes/" + PROJECT_FILE;
//        FileUtils.mkdir( configPropertiesFilePath.substring( 0, configPropertiesFilePath.lastIndexOf( '/' ) ) );
//
//        try {
//            // Write deploy time to loadTime property in project.properties
//            Properties props = new Properties();
//            props.load( new FileInputStream( configPropertiesFilePath ) );
//            props.setProperty( Project.LOAD_TIME_KEY, Utils.getTimestamp( new Date() ) );
//
//            OutputStream out = new FileOutputStream( configPropertiesFilePath );
//            props.store( out, "Generated by chop:deploy" );
//        }
//        catch ( JsonMappingException e ) {
//            throw new MojoExecutionException( "Project.json file format is faulty", e );
//        }
//        catch ( IOException e ) {
//            throw new MojoExecutionException( "Error while modifying " + PROJECT_FILE + " file", e );
//        }
//
//
//        AmazonS3 s3 = Utils.getS3Client( accessKey, secretKey );
//
//        if ( !s3.doesBucketExist( bucketName ) ) {
//            Bucket bucket = s3.createBucket( bucketName );
//            if( bucket == null ) {
//                throw new MojoExecutionException( "Bucket " + bucketName + " doesn't exist and could not create one" );
//            }
//            else {
//                getLog().info( "Bucket " + bucketName + " didn't exist, created a new one" );
//            }
//        }
//
//        getLog().info( "Uploading file to: " + destinationFile );
//
//        boolean success = Utils.uploadToS3( s3, bucketName, destinationFile, source );
//        if ( !success ) {
//            throw new MojoExecutionException( "Unable to upload war file to S3." );
//        }
//
//        success = Utils.uploadToS3( s3, bucketName, projectFileKey, new File( configPropertiesFilePath ) );
//        if ( !success ) {
//            throw new MojoExecutionException( "Unable to upload $PROJECT_FILE file to S3." );
//        }
//
//        getLog().info( "File " + source + " uploaded to s3://" + bucketName + "/" + destinationFile );
    }


    private boolean isReadyToDeploy() {
        File source = new File( getRunnerToUploadPath() );
        try {
            if ( ! source.exists() ) {
                return false;
            }

            File extractedConfigPropFile = new File( getExtractedWarRootPath() + "WEB-INF/classes/project.properties" );
            if ( extractedConfigPropFile.exists() ) {
                Properties props = new Properties();
                FileInputStream inputStream = new FileInputStream( extractedConfigPropFile );
                props.load( inputStream );
                inputStream.close();

                String commitId = Utils.getLastCommitUuid( Utils.getGitConfigFolder( getProjectBaseDirectory() ) );
                return commitId.equals( props.getProperty( Project.GIT_UUID_KEY ) );
            }
        } catch ( Exception e ) {
            getLog().warn( e );
        }
        return false;
    }
}
