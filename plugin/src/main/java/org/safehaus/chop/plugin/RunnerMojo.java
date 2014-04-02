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


@Mojo( name = "runner", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class RunnerMojo extends MainMojo {

    @SuppressWarnings( "UnusedDeclaration" )
    public RunnerMojo() {

    }

    protected RunnerMojo( MainMojo mojo ) {
        this.username = mojo.username;
        this.password = mojo.password;
        this.endpoint = mojo.endpoint;
        this.testPackageBase = mojo.testPackageBase;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.runnerCount = mojo.runnerCount;
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

            // Extract the original chop-runner.jar file which should be in the local repository
            String runnerJarPath = getRunnerInLocalRepo();
            String extractedRunnerPath = getExtractedRunnerPath();
            if ( FileUtils.fileExists( extractedRunnerPath ) ) {
                FileUtils.cleanDirectory( extractedRunnerPath );
            }
            else {
                FileUtils.mkdir( extractedRunnerPath );
            }
            Utils.extractJar( new File( runnerJarPath ), extractedRunnerPath );

            // Copy caller project jar and its dependency jars to topmost runner folder
            File libPathFile = new File( extractedRunnerPath );
            String projectTestOutputJar = getProjectTestOutputJarPath();
            if ( ! FileUtils.fileExists( projectTestOutputJar ) ) {
                throw new MojoExecutionException( "Project Test Jar could not be found. Make sure you use 'test-jar'" +
                        " goal of the 'maven-jar-plugin' in your project's pom" );
            }
            FileUtils.copyFileToDirectory( new File( getProjectOutputJarPath() ), libPathFile );
            FileUtils.copyFileToDirectory( new File( projectTestOutputJar ), libPathFile );

            Utils.copyArtifactsTo( this.project, extractedRunnerPath );

            // Create project.properties file
            InputStream inputStream;
            Properties prop = new Properties();
            String configPropertiesFilePath = extractedRunnerPath + "project.properties";
            if ( FileUtils.fileExists( configPropertiesFilePath ) ) {
                // Existing project.properties of chop-runner
                inputStream = new FileInputStream( configPropertiesFilePath );
                prop.load( inputStream );
                inputStream.close();
            }

            // If exists, properties in this file can overwrite the ones from chop-runner
            if ( getClass().getResource( "project.properties" ) != null ) {
                inputStream = getClass().getResourceAsStream( "project.properties" );
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
            String md5 = Utils.getMD5( timeStamp, commitId );
            prop.setProperty( Project.GIT_UUID_KEY, commitId );
            prop.setProperty( Project.GIT_URL_KEY, gitUrl );
            prop.setProperty( Project.CREATE_TIMESTAMP_KEY, timeStamp );
            prop.setProperty( Project.GROUP_ID_KEY, this.project.getGroupId() );
            prop.setProperty( Project.ARTIFACT_ID_KEY, this.project.getArtifactId() );
            prop.setProperty( Project.PROJECT_VERSION_KEY, this.project.getVersion() );
            prop.setProperty( Project.TEST_PACKAGE_BASE, testPackageBase );
            prop.setProperty( Project.MD5_KEY, md5 );

            String uuid = commitId.substring( 0, CHARS_OF_UUID/2 ) +
                    commitId.substring( commitId.length() - CHARS_OF_UUID/2 );

            prop.setProperty( Project.LOAD_KEY, TESTS_PATH + '/' + uuid + '/' + RUNNER_JAR );
            prop.setProperty( Project.LOAD_TIME_KEY, String.valueOf( System.currentTimeMillis() ) );
            prop.setProperty( Project.CHOP_VERSION_KEY, plugin.getVersion() );

            // Save the newly formed properties file under project.properties
            FileUtils.mkdir( configPropertiesFilePath.substring( 0, configPropertiesFilePath.lastIndexOf( '/' ) ) );
            FileWriter writer = new FileWriter( configPropertiesFilePath );
            prop.store( writer, "Generated with chop:runner" );

            // Create the final runner file
            String finalPath = getRunnerFile().getAbsolutePath();
            File finalFile = new File( finalPath );
            Utils.archiveWar( finalFile, extractedRunnerPath );
        }
        catch ( MojoExecutionException e ) {
            throw e;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new MojoExecutionException( "Error while executing plugin", e );
        }
    }
}
