/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.apache.usergrid.chop.api.Constants;
import org.apache.usergrid.chop.api.Project;
import org.apache.usergrid.chop.api.ProjectBuilder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


/**
 * This is the parent class for all chop plugin goal classes, takes the configuration parameters from caller
 * module's pom and provides extended get methods for several file paths that will be used by extended classes
 */
public class MainMojo extends AbstractMojo implements Constants {

    static {
        System.setProperty( "javax.net.ssl.trustStore", "jssecacerts" );
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;


    @Parameter( defaultValue = "${plugin}", readonly = true )
    protected PluginDescriptor plugin;


    @Parameter( defaultValue = "${settings.localRepository}" )
    protected String localRepository;


    /**
     * Leaving this parameter with the default 'true' value causes the plugin goal to fail when there are modified
     * sources in the local git repository.
     */
    @Parameter( property = "failIfCommitNecessary", defaultValue = "true" )
    protected boolean failIfCommitNecessary;


    /**
     * Number of instances that the runner will be running on for tests
     */
    @Parameter( property = "runnerCount", defaultValue = "3" )
    protected Integer runnerCount;


    /**
     * Endpoint URL of the coordinator
     */
    @Parameter( property = "endpoint", required = true )
    protected String endpoint;


    /**
     * User name that will be used when hitting the coordinator endpoint
     */
    @Parameter( property = "username", required = true )
    protected String username;


    /**
     * Password that will be used when hitting the coordinator endpoint
     */
    @Parameter( property = "password", required = true )
    protected String password;


    /**
     * This is the package base to use when scanning for chopped tests.
     */
    @Parameter( property = "testPackageBase", required = true )
    protected String testPackageBase;


    /**
     * @todo is this still necessary?
     *
     * Leaving this null will default to the use of "changeit" for the passphrase.
     */
    @Parameter( property = "certStorePassphrase" )
    protected String certStorePassphrase;


    @Parameter( property = "finalName", defaultValue = "${project.artifactId}-${project.version}-chop" )
    protected String finalName;

    protected static ExecutorService executor;


    @Override
    public void execute() throws MojoExecutionException {
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected MainMojo( MainMojo mojo ) {
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


    protected MainMojo() {
    }


    public String getProjectOutputJarPath() {
        return Utils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "." + project.getPackaging();
    }


    public String getProjectTestOutputJarPath() {
        return Utils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "-tests.jar";
    }


    /** @return Returns the project base directory with a '/' at the end */
    public String getProjectBaseDirectory() {
        return Utils.forceSlashOnDir( project.getBasedir().getAbsolutePath() );
    }


    /** @return Returns the extracted path of RUNNER_JAR file with a '/' at the end */
    public String getExtractedRunnerPath() {
        return getProjectBaseDirectory() + "target/runner/";
    }


    /** @return Returns the full path of created runner.war file */
    public File getRunnerFile() {
        return new File( project.getBuild().getDirectory(), finalName + ".jar" );
    }


    /** @return Returns the full path of created project.json file */
    public String getProjectFileToUploadPath() {
        return getExtractedRunnerPath() + "WEB-INF/classes/" + PROJECT_FILE;
    }


    private String getShortUuid() throws MojoExecutionException {
        String uuid = Utils.getLastCommitUuid( Utils.getGitConfigFolder( project.getBasedir().getParent() ) );
        return uuid.substring( 0, CHARS_OF_UUID/2 ) + uuid.substring( uuid.length() - CHARS_OF_UUID/2 );
    }


    /** @return Returns the full path of the original chop-runner jar file in the local maven repository */
    public String getRunnerInLocalRepo() {
        String path = localRepository;
        Artifact chopPluginArtifact = plugin.getPluginArtifact();

        path += "/" + chopPluginArtifact.getGroupId().replace( '.', '/' ) + "/chop-runner/" +
                chopPluginArtifact.getVersion() + "/chop-runner-" + chopPluginArtifact.getVersion() + ".jar";

        return path;
    }


    /**
     * Loads the project configuration data if available.
     *
     * @return the Project for this project or blow chunks
     * @throws MojoExecutionException the chunks we blow
     */
    public Project loadProjectConfiguration() throws MojoExecutionException {
        File projectFile = new File( getProjectFileToUploadPath() );
        if ( ! projectFile.exists() ) {
            getLog().warn( "It seems as though the project properties file " + projectFile
                    + " does not exist. Creating it and the war now." );
            RunnerMojo runnerMojo = new RunnerMojo( this );
            runnerMojo.execute();

            if ( projectFile.exists() ) {
                getLog().info( "War is generated and project file exists." );
            }
            else {
                throw new MojoExecutionException( "Failed to generate the project.properties." );
            }
        }

        // Load the project configuration from the file system
        Project project;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( projectFile ) );
            ProjectBuilder builder = new ProjectBuilder( props );
            project = builder.getProject();
        }
        catch ( Exception e ) {
            getLog().warn( "Error accessing project information from local filesystem: " + getProjectFileToUploadPath(),
                    e );
            throw new MojoExecutionException(
                    "Cannot access local file system based project information: " + getProjectFileToUploadPath(), e );
        }

        return project;
    }
}
