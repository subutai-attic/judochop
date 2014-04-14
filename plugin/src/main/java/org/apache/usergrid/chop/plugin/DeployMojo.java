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

import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.usergrid.chop.api.Project;
import org.apache.usergrid.chop.api.RestParams;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;


/** Deploys the jar created by runner goal to coordinator using supplied configuration parameters */
@Mojo(name = "deploy", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class DeployMojo extends MainMojo {


    public DeployMojo() {

    }


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

        File source = getRunnerFile();
        if ( source.exists() ) {
            getLog().info( source.getAbsolutePath() + " exists!" );
        }
        else {
            getLog().info( source.getAbsolutePath() + " does not exist." );
        }

        if ( ! isReadyToDeploy() ) {
            getLog().info( RUNNER_JAR + " is NOT present to upload, calling chop:runner goal now..." );
            RunnerMojo runnerMojo = new RunnerMojo( this );
            runnerMojo.execute();
        }

        if ( ! isReadyToDeploy() ) {
            throw new MojoExecutionException( "Files to be deployed are not ready and chop:runner failed" );
        }

        /** Prepare the POST content for upload */

        Properties props = new Properties();
        try {
            File extractedConfigPropFile = new File( getExtractedRunnerPath(), PROJECT_FILE );
            FileInputStream inputStream = new FileInputStream( extractedConfigPropFile );
            props.load( inputStream );
            inputStream.close();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            getLog().error( "Error while reading project.properties in runner.jar" );
            throw new MojoExecutionException( e.getMessage() );
        }

        MimeMultipart multipart = new MimeMultipart();

        try {

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.COMMIT_ID );
            bodyPart.setText( props.getProperty( Project.GIT_UUID_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.MODULE_ARTIFACTID );
            bodyPart.setText( props.getProperty( Project.ARTIFACT_ID_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.MODULE_GROUPID );
            bodyPart.setText( props.getProperty( Project.GROUP_ID_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.MODULE_VERSION );
            bodyPart.setText( props.getProperty( Project.PROJECT_VERSION_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.USERNAME );
            bodyPart.setText( username );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.FILENAME );
            bodyPart.setText( RUNNER_JAR );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.VCS_REPO_URL );
            bodyPart.setText( props.getProperty( Project.GIT_URL_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.TEST_PACKAGE );
            bodyPart.setText( props.getProperty( Project.TEST_PACKAGE_BASE ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart();
            bodyPart.setContentID( RestParams.MD5 );
            bodyPart.setText( props.getProperty( Project.MD5_KEY ) );
            multipart.addBodyPart( bodyPart );

            bodyPart = new MimeBodyPart( new FileInputStream( source ) );
            bodyPart.setContentID( RestParams.CONTENT );
            multipart.addBodyPart( bodyPart );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            getLog().error( "Error while preparing upload data" );
            throw new MojoExecutionException( e.getMessage() );
        }

        /** Upload */
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create( clientConfig );
        WebResource resource = client.resource( endpoint ).path( "/upload" );

        ClientResponse resp = resource.path( "/runner" )
                                .accept( MediaType.TEXT_PLAIN )
                                .post( ClientResponse.class, multipart );

        if( resp.getStatus() == Response.Status.CREATED.getStatusCode() ) {
            getLog().info( "Runner Jar uploaded to the coordinator successfully on path: " +
                    resp.getEntity( String.class ) );
        }
        else {
            getLog().error( "Could not upload successfully, HTTP status: " + resp.getStatus() );
            getLog().error( "Error Message: " + resp.getEntity( String.class ) );

            throw new MojoExecutionException( "Upload failed" );
        }
    }


    private boolean isReadyToDeploy() {
        File source = getRunnerFile();
        try {
            if ( ! source.exists() ) {
                return false;
            }

            File extractedConfigPropFile = new File( getExtractedRunnerPath(), PROJECT_FILE);
            if ( extractedConfigPropFile.exists() ) {
                Properties props = new Properties();
                FileInputStream inputStream = new FileInputStream( extractedConfigPropFile );
                props.load( inputStream );
                inputStream.close();

                String commitId = Utils.getLastCommitUuid( Utils.getGitConfigFolder( getProjectBaseDirectory() ) );

                /** If failIfCommitNecessary set to false, no need to force rebuild with different commit id */
                return ( ! failIfCommitNecessary ) || commitId.equals( props.getProperty( Project.GIT_UUID_KEY ) );
            }
        } catch ( Exception e ) {
            getLog().warn( e );
        }
        return false;
    }
}
