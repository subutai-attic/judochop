package org.safehaus.perftest.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.codehaus.plexus.util.FileUtils;
import org.safehaus.perftest.api.TestInfo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.fasterxml.jackson.databind.ObjectMapper;


/** Creates perftest.war using perftest-server module and caller module */
@Mojo(name = "war", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST)
public class PerftestWarMojo extends PerftestMojo {


    protected PerftestWarMojo( PerftestMojo mojo ) {
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
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    protected PerftestWarMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {

        String gitConfigDirectory = PerftestUtils.getGitConfigFolder( getProjectBaseDirectory() );
        String commitId = PerftestUtils.getLastCommitUuid( gitConfigDirectory );

        if ( failIfCommitNecessary && PerftestUtils.isCommitNecessary( gitConfigDirectory ) ) {
            String failMsg = "There are modified sources, commit changes before calling the plugin or set "
                    + "failIfCommitNecessary parameter as false in your plugin configuration field inside the pom.xml";

            throw new MojoExecutionException( failMsg );
        }

        try {

            String timeStamp = PerftestUtils.getTimestamp( new Date() );

            // Extract the war file
            String serverWarPath = getServerWarPath();
            String extractedWarRoot = getExtractedWarRootPath();
            if ( FileUtils.fileExists( extractedWarRoot ) ) {
                FileUtils.cleanDirectory( extractedWarRoot );
            }
            else {
                FileUtils.mkdir( extractedWarRoot );
            }
            PerftestUtils.extractWar( new File( serverWarPath ), extractedWarRoot );

            // Copy caller project jar and its dependency jars to WEB-INF/lib folder
            String libPath = extractedWarRoot + "WEB-INF/lib/";
            FileUtils.copyFileToDirectory( new File( getProjectOutputJarPath() ), new File( libPath ) );
            PerftestUtils.copyArtifactsTo( project, libPath, true );

            // Create config.properties file
            InputStream inputStream;
            Properties prop = new Properties();
            String configPropertiesFilePath = extractedWarRoot + "WEB-INF/classes/config.properties";
            if ( FileUtils.fileExists( configPropertiesFilePath ) ) {
                // Existing config.properties of perftest-server
                inputStream = new FileInputStream( configPropertiesFilePath );
                prop.load( inputStream );
                inputStream.close();
            }

            // If exists, properties in this file can overwrite the ones from perftest-server
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
            String gitUrl = PerftestUtils.getGitRemoteUrl( gitConfigDirectory );
            String warMd5 = PerftestUtils.getMD5( timeStamp, commitId );
            prop.setProperty( GIT_UUID_KEY, commitId );
            prop.setProperty( GIT_URL_KEY, gitUrl );
            prop.setProperty( CREATE_TIMESTAMP_KEY, timeStamp );
            prop.setProperty( GROUP_ID_KEY, project.getGroupId() );
            prop.setProperty( ARTIFACT_ID_KEY, project.getArtifactId() );
            prop.setProperty( PROJECT_VERSION_KEY, project.getVersion() );
            prop.setProperty( TEST_MODULE_FQCN_KEY, testModuleFQCN );
            prop.setProperty( AWS_BUCKET_KEY, bucketName );
            prop.setProperty( AWSKEY_KEY, accessKey );
            prop.setProperty( AWS_SECRET_KEY, secretKey );
            prop.setProperty( "manager.app.username", managerAppUsername );
            prop.setProperty( "manager.app.password", managerAppPassword );
            prop.setProperty( WAR_MD5_KEY, warMd5 );

            // Save the newly formed properties file under WEB-INF/classes/config.properties
            FileUtils.mkdir( configPropertiesFilePath.substring( 0, configPropertiesFilePath.lastIndexOf( '/' ) ) );
            FileWriter writer = new FileWriter( configPropertiesFilePath );
            prop.store( writer, null );

            // Create the final WAR
            String finalWarPath = getWarToUploadPath();
            File finalWarFile = new File( finalWarPath );
            PerftestUtils.archiveWar( finalWarFile, extractedWarRoot );

            // Generate the test-info.json file
            TestInfo testInfo = new TestInfo();
            testInfo.setTestModuleFQCN( testModuleFQCN );
            testInfo.setCreateTimestamp( timeStamp );
            testInfo.setArtifactId( project.getArtifactId() );
            testInfo.setProjectVersion( project.getVersion() );
            testInfo.setGroupId( project.getGroupId() );
            testInfo.setGitRepoUrl( gitUrl );
            testInfo.setGitUuid( commitId );
            testInfo.setLoadKey( "tests/" + commitId + "/perftest.war" );
            testInfo.setPerftestVersion( prop.getProperty( PERFTEST_VERSION_KEY ) );
            testInfo.setWarMd5( warMd5 );

            ObjectMapper mapper = new ObjectMapper();
            File testInfoFile = new File( getTestInfoToUploadPath() );
            mapper.writeValue( testInfoFile, testInfo );
        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Error while executing plugin", e );
        }
    }
}
