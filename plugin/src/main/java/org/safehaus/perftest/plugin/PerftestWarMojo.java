package org.safehaus.perftest.plugin;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.*;

import org.codehaus.plexus.util.FileUtils;
import org.safehaus.perftest.api.TestInfoImpl;
import org.safehaus.perftest.api.settings.ConfigKeys;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Creates perftest.war using perftest-webapp module and caller module
 */
@Mojo( name = "war", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class PerftestWarMojo extends PerftestMojo {


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
            String webappWarPath = getWebappWarPath();
            String extractedWarRoot = getExtractedWarRootPath();
            if ( FileUtils.fileExists( extractedWarRoot ) ) {
                FileUtils.cleanDirectory( extractedWarRoot );
            }
            else {
                FileUtils.mkdir( extractedWarRoot );
            }
            PerftestUtils.extractWar( new File( webappWarPath ), extractedWarRoot );

            // Copy caller project jar and its dependency jars to WEB-INF/lib folder
            String libPath = extractedWarRoot + "WEB-INF/lib/";
            FileUtils.copyFileToDirectory( new File( getProjectOutputJarPath() ), new File( libPath ) );
            PerftestUtils.copyArtifactsTo( project, libPath, true );

            // Create config.properties file
            InputStream inputStream;
            Properties prop = new Properties();
            String configPropertiesFilePath = extractedWarRoot + "WEB-INF/classes/config.properties";
            if ( FileUtils.fileExists( configPropertiesFilePath ) ) {
                // Existing config.properties of webapp-perftest
                inputStream = new FileInputStream( configPropertiesFilePath );
                prop.load( inputStream );
                inputStream.close();
            }

            // If exists, properties in this file can overwrite the ones from webapp-perftest
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
            prop.setProperty( "git.uuid", commitId );
            prop.setProperty( "git.url", gitUrl );
            prop.setProperty( "create.timestamp", timeStamp );
            prop.setProperty( "group.id", project.getGroupId() );
            prop.setProperty( "artifact.id", project.getArtifactId() );
            prop.setProperty( "project.version", project.getVersion() );
            prop.setProperty( "test.module.fqcn", testModuleFQCN );
            prop.setProperty( "aws.s3.bucket", bucketName );
            prop.setProperty( "aws.s3.key", accessKey );
            prop.setProperty( "aws.s3.secret", secretKey );
            prop.setProperty( "manager.app.username", managerAppUsername );
            prop.setProperty( "manager.app.password", managerAppPassword );
            prop.setProperty( "war.md5", warMd5 );

            // Save the newly formed properties file under WEB-INF/classes/config.properties
            FileUtils.mkdir( configPropertiesFilePath.substring( 0, configPropertiesFilePath.lastIndexOf('/') ) );
            FileWriter writer = new FileWriter( configPropertiesFilePath );
            prop.store( writer, null );

            // Create the final WAR
            String finalWarPath = getWarToUploadPath();
            File finalWarFile = new File( finalWarPath );
            PerftestUtils.archiveWar( finalWarFile, extractedWarRoot );

            // Generate the test-info.json file
            TestInfoImpl testInfo = new TestInfoImpl();
            testInfo.setTestModuleFQCN( testModuleFQCN );
            testInfo.setCreateTimestamp( timeStamp );
            testInfo.setArtifactId( project.getArtifactId() );
            testInfo.setProjectVersion( project.getVersion() );
            testInfo.setGroupId( project.getGroupId() );
            testInfo.setGitRepoUrl( gitUrl );
            testInfo.setGitUuid( commitId );
            testInfo.setLoadKey( "tests/" + commitId + "/perftest.war" );
            testInfo.setPerftestVersion( prop.getProperty( ConfigKeys.PERFTEST_VERSION_KEY ) );
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
