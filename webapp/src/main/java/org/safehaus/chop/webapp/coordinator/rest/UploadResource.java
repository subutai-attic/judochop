/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.safehaus.chop.webapp.coordinator.rest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.stack.BasicStack;
import org.safehaus.chop.stack.Stack;
import org.safehaus.chop.webapp.ChopUiFig;
import org.safehaus.chop.webapp.coordinator.Coordinator;
import org.safehaus.chop.webapp.dao.CommitDao;
import org.safehaus.chop.webapp.dao.ModuleDao;
import org.safehaus.chop.webapp.dao.ProviderParamsDao;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.dao.UserDao;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.dao.model.BasicModule;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.stack.User;
import org.safehaus.chop.webapp.elasticsearch.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.multipart.FormDataParam;


/**
 * REST operation to upload (a.k.a. deploy) a project war file.
 */
@Singleton
@Produces( MediaType.TEXT_PLAIN )
@Path( UploadResource.ENDPOINT_URL )
public class UploadResource implements RestParams {
    public final static String ENDPOINT_URL = "/upload";
    private final static Logger LOG = LoggerFactory.getLogger( UploadResource.class );


    @Inject
    private Coordinator coordinator;

    @Inject
    private ChopUiFig chopUiFig;

    @Inject
    private RestFig restFig;

    @Inject
    private UserDao userDao;

    @Inject
    private ProviderParamsDao providerParamsDao;

    @Inject
    private ModuleDao moduleDao;

    @Inject
    private RunDao runDao;

    @Inject
    private RunResultDao runResultDao;

    @Inject
    private CommitDao commitDao;



    @POST
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    public Response upload( MimeMultipart multipart )
    {
        try {
            String filename = multipart.getBodyPart(0).getContent().toString();
            LOG.warn("FILENAME: " + filename);
            InputStream in = multipart.getBodyPart(1).getInputStream();
            String fileLocation = /* config.getWarUploadPath() + */ filename;
            writeToFile(in, fileLocation);
        } catch (MessagingException ex) {
            LOG.error("upload", ex);
        } catch (IOException ex) {
            LOG.error("upload", ex);
        }
        return Response.status(Response.Status.CREATED).entity("ok").build();
    }


    @POST
    @Path( "/runner" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    @Produces( MediaType.TEXT_PLAIN )
    public Response uploadRunner( MimeMultipart multipart ) throws Exception
    {
        LOG.warn( "/upload/runner called ..." );

        String commitId = multipart.getBodyPart( RestParams.COMMIT_ID ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.COMMIT_ID, commitId );

        String artifactId = multipart.getBodyPart( RestParams.MODULE_ARTIFACTID ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.MODULE_ARTIFACTID, artifactId );

        String groupId = multipart.getBodyPart( RestParams.MODULE_GROUPID ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.MODULE_GROUPID, groupId );

        String version = multipart.getBodyPart( RestParams.MODULE_VERSION ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.MODULE_VERSION, version );

        String username = multipart.getBodyPart( RestParams.USERNAME ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.USERNAME, username );

        String filename = multipart.getBodyPart( RestParams.FILENAME ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.FILENAME, username );

        String vcsRepoUrl = multipart.getBodyPart( RestParams.VCS_REPO_URL ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.VCS_REPO_URL, vcsRepoUrl );

        String testPackage = multipart.getBodyPart( RestParams.TEST_PACKAGE ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.TEST_PACKAGE, testPackage );

        String md5 = multipart.getBodyPart( RestParams.MD5 ).getContent().toString();
        LOG.debug( "extracted {} = {}", RestParams.MD5, md5 );

        User user = userDao.get( username );
        ProviderParams providerParams = providerParamsDao.getByUser( username );
        InputStream in = multipart.getBodyPart( RestParams.CONTENT ).getInputStream();

        /*
         * File storage scheme:
         *
         * ${base_for_files}/${user}/${groupId}/${artifactId}/${version}/${commitId}/runner.jar
         */

        File parentDir = new File( chopUiFig.getContextTempDir() );
        parentDir = new File( parentDir, username );
        parentDir = new File( parentDir, groupId );
        parentDir = new File( parentDir, artifactId );
        parentDir = new File( parentDir, version );
        parentDir = new File( parentDir, commitId );

        if ( ! parentDir.exists() ) {
            if ( parentDir.mkdirs() ) {
                LOG.info( "Created parent directory {} for uploaded runner file", parentDir.getAbsolutePath() );
            }
            else {
                String errorMessage = "Failed to create parent directory " + parentDir.getAbsolutePath()
                        + " for uploaded runner file.";
                LOG.error( errorMessage );
                return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( errorMessage ).build();
            }
        }

        // Download and write the file to the proper position on disk & reference
        File runnerFile = new File( parentDir, filename );
        writeToFile( in, runnerFile.getAbsolutePath() );

        // - this is bad news because we will get commits of other users :(
        // - we also need to qualify the commit with username, groupId,
        //   and the version of module as well

        Commit commit = null;
        Module module = null;

        List<Commit> commits = commitDao.getByModule( artifactId );
        for ( Commit returnedCommit : commits ) {
            Module commitModule = moduleDao.get( returnedCommit.getModuleId() );
            if ( commitModule.getArtifactId().equals( artifactId ) &&
                 commitModule.getGroupId().equals( groupId ) &&
                 commitModule.getVersion().equals( version ) )
            {
                commit = returnedCommit;
                module = commitModule;
            }
        }

        if ( module == null ) {
            module = new BasicModule( groupId, artifactId, version, vcsRepoUrl, testPackage );
            moduleDao.save( module );
        }

        if ( commit == null ) {
            commit = new BasicCommit( commitId, module.getId(), md5, new Date(), runnerFile.getAbsolutePath() );
            commitDao.save( commit );
        }

        // Access the jar file resources after adding it to a new ClassLoader
        URLClassLoader classLoader = new URLClassLoader( new URL[] { runnerFile.toURL() },
                Thread.currentThread().getContextClassLoader() );
        ObjectMapper mapper = new ObjectMapper();
        Stack stack = mapper.readValue( classLoader.getResourceAsStream( "stack.json" ), BasicStack.class );

        return Response.status( Response.Status.CREATED ).entity( runnerFile.getAbsoluteFile() ).build();
    }




//    @SuppressWarnings( "unchecked" )
    @POST
    @Path( "/results" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    @Produces( MediaType.TEXT_PLAIN )
    public Response uploadResults(
        @FormDataParam( CONTENT ) InputStream in,
        @FormDataParam( RUN_ID ) String runId ) throws Exception
    {
        JSONObject object = ( JSONObject ) new JSONParser().parse( new InputStreamReader( in ) );
        JSONArray runResults = ( JSONArray ) object.get( "runResults" );
        Iterator<JSONObject> iterator = runResults.iterator();

        //noinspection WhileLoopReplaceableByForEach
        while( iterator.hasNext() ) {
            JSONObject jsonResult = iterator.next();

            BasicRunResult runResult = new BasicRunResult(
                    runId,
                    Util.getInt( jsonResult, "runCount" ),
                    Util.getInt( jsonResult, "runTime" ),
                    Util.getInt( jsonResult, "ignoreCount" ),
                    Util.getInt( jsonResult, "failureCount" )
            );

            if ( runResultDao.save( runResult ) ) {
                LOG.info( "Saved run result: {}", runResult );
            }
        }

        return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
    }


//    @SuppressWarnings( "unchecked" )
    @POST
    @Path( "/summary" )
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    @Produces( MediaType.TEXT_PLAIN )
    public Response uploadSummary( @FormDataParam( CONTENT ) InputStream in,
                                   @FormDataParam( RUNNER_HOSTNAME ) String runnerHostname ) throws Exception
    {
        JSONObject json = ( JSONObject ) new JSONParser().parse( new InputStreamReader( in ) );
        BasicRun run = new BasicRun(
                COMMIT_ID,
                runnerHostname,
                Util.getInt( json, "runNumber" ),
                Util.getString( json, "testName" ) );
        run.copyJson( json );

        if ( runDao.save( run ) ) {
            LOG.info( "Created new Run {} ", run );
        }
        else {
            LOG.warn( "Failed to create new Run" );
        }

        return Response.status( Response.Status.CREATED ).entity( run.getId() ).build();
    }


    private void writeToFile( InputStream in, String fileLocation )
    {
        OutputStream out = null;

        try
        {
            int read;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream( fileLocation );

            while ( ( read = in.read( bytes ) ) != -1 )
            {
                out.write( bytes, 0, read );
            }
            out.flush();
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to write out file: " + fileLocation, e );
        }
        finally
        {
            if ( out != null ) {
                try {
                    out.close();
                }
                catch ( IOException e ) {
                    LOG.error( "Failed while trying to close output stream for {}", fileLocation );
                }
            }
        }
    }
}
