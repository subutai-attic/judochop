package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.codehaus.plexus.util.FileUtils;
import org.safehaus.chop.api.Project;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.fasterxml.jackson.databind.ObjectMapper;


/** Creates perftest.war using perftest-runner module and caller module */
@Mojo(name = "war", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST)
public class WarMojo extends MainMojo {


    protected WarMojo( MainMojo mojo ) {
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


    protected WarMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {

        String gitConfigDirectory = Utils.getGitConfigFolder( getProjectBaseDirectory() );
        String commitId = Utils.getLastCommitUuid( gitConfigDirectory );

        if ( failIfCommitNecessary && Utils.isCommitNecessary( gitConfigDirectory ) ) {
            String failMsg = "There are modified sources, commit changes before calling the plugin or set "
                    + "failIfCommitNecessary parameter as false in your plugin configuration field inside the pom.xml";

            throw new MojoExecutionException( failMsg );
        }

        try {

            String timeStamp = Utils.getTimestamp( new Date() );

            // Extract the war file
            String serverWarPath = getServerWarPath();
            String extractedWarRoot = getExtractedWarRootPath();
            if ( FileUtils.fileExists( extractedWarRoot ) ) {
                FileUtils.cleanDirectory( extractedWarRoot );
            }
            else {
                FileUtils.mkdir( extractedWarRoot );
            }
            Utils.extractWar( new File( serverWarPath ), extractedWarRoot );

            // Copy caller project jar and its dependency jars to WEB-INF/lib folder
            String libPath = extractedWarRoot + "WEB-INF/lib/";
            File libPathFile = new File( libPath );
            String projectTestOutputJar = getProjectTestOutputJarPath();
            if ( ! FileUtils.fileExists( projectTestOutputJar ) ) {
                throw new MojoExecutionException( "Project Test Jar could not be found. Make sure you use 'test-jar'" +
                        " goal of the 'maven-jar-plugin' in your project's pom" );
            }
            FileUtils.copyFileToDirectory( new File( getProjectOutputJarPath() ), libPathFile );
            FileUtils.copyFileToDirectory( new File( projectTestOutputJar ), libPathFile );
            Utils.copyArtifactsTo( this.project, libPath, false );

            // Create config.properties file
            InputStream inputStream;
            Properties prop = new Properties();
            String configPropertiesFilePath = extractedWarRoot + "WEB-INF/classes/config.properties";
            if ( FileUtils.fileExists( configPropertiesFilePath ) ) {
                // Existing config.properties of perftest-runner
                inputStream = new FileInputStream( configPropertiesFilePath );
                prop.load( inputStream );
                inputStream.close();
            }

            // If exists, properties in this file can overwrite the ones from perftest-runner
            if ( getClass().getResource( "config.properties" ) != null ) {
                inputStream = getClass().getResourceAsStream( "config.properties" );
                Properties propCurrent = new Properties();
                propCurrent.load( inputStream );
                inputStream.close();

                String key;
                while ( propCurrent.propertyNames().hasMoreElements() ) {
                    key = propCurrent.propertyNames().nextElement().toString();
                    prop.setProperty( key, propCurrent.getProperty( key ) );
                }
            }

            // Insert all properties acquired in runtime and overwrite existing ones
            String gitUrl = Utils.getGitRemoteUrl( gitConfigDirectory );
            String warMd5 = Utils.getMD5( timeStamp, commitId );
            prop.setProperty( GIT_UUID_KEY, commitId );
            prop.setProperty( GIT_URL_KEY, gitUrl );
            prop.setProperty( CREATE_TIMESTAMP_KEY, timeStamp );
            prop.setProperty( GROUP_ID_KEY, this.project.getGroupId() );
            prop.setProperty( ARTIFACT_ID_KEY, this.project.getArtifactId() );
            prop.setProperty( PROJECT_VERSION_KEY, this.project.getVersion() );
            prop.setProperty( TEST_PACKAGE_BASE, testPackageBase );
            prop.setProperty( AWS_BUCKET_KEY, bucketName );
            prop.setProperty( AWSKEY_KEY, accessKey );
            prop.setProperty( AWS_SECRET_KEY, secretKey );
            prop.setProperty( "manager.app.username", managerAppUsername );
            prop.setProperty( "manager.app.password", managerAppPassword );
            prop.setProperty( WAR_MD5_KEY, warMd5 );

            String uuid = commitId.substring( 0, CHARS_OF_UUID/2 ) +
                    commitId.substring( commitId.length() - CHARS_OF_UUID/2 );

            prop.setProperty( LOAD_KEY, CONFIGS_PATH + '/' + uuid + '/' + RUNNER_WAR );
            prop.setProperty( LOAD_TIME_KEY, String.valueOf( System.currentTimeMillis() ) );
            prop.setProperty( CHOP_VERSION_KEY, plugin.getVersion() );

            // Save the newly formed properties file under WEB-INF/classes/config.properties
            FileUtils.mkdir( configPropertiesFilePath.substring( 0, configPropertiesFilePath.lastIndexOf( '/' ) ) );
            FileWriter writer = new FileWriter( configPropertiesFilePath );
            prop.store( writer, null );

            // Create the final WAR
            String finalWarPath = getWarToUploadPath();
            File finalWarFile = new File( finalWarPath );
            Utils.archiveWar( finalWarFile, extractedWarRoot );

            // Generate the test-info.json file
            Project project = new Project();
            project.setTestPackageBase( testPackageBase );
            project.setCreateTimestamp( timeStamp );
            project.setArtifactId( this.project.getArtifactId() );
            project.setProjectVersion( this.project.getVersion() );
            project.setGroupId( this.project.getGroupId() );
            project.setVcsRepoUrl( gitUrl );
            project.setVcsVersion( commitId );
            project.setLoadKey( CONFIGS_PATH + "/" + commitId + "/" + RUNNER_WAR );
            project.setChopVersion( plugin.getVersion() );
            project.setWarMd5( warMd5 );

            ObjectMapper mapper = new ObjectMapper();
            File projectFile = new File( getProjectFileToUploadPath() );
            mapper.writeValue( projectFile, project );
        }
        catch ( MojoExecutionException e ) {
            throw e;
        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Error while executing plugin", e );
        }
    }
}
