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
package org.safehaus.chop.webapp.rest;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.safehaus.chop.api.RestParams;
import org.safehaus.chop.webapp.dao.RunDao;
import org.safehaus.chop.webapp.dao.RunResultDao;
import org.safehaus.chop.webapp.dao.model.BasicRun;
import org.safehaus.chop.webapp.dao.model.BasicRunResult;
import org.safehaus.chop.webapp.elasticsearch.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private RestFig config;

    @Inject
    private RunDao runDao;

    @Inject
    private RunResultDao runResultDao;


//    @POST
//    @Consumes( MediaType.MULTIPART_FORM_DATA )
//    @Produces( MediaType.TEXT_PLAIN )
//    public Response upload(
//            @FormDataParam( CONTENT ) InputStream in,
//            @FormDataParam( FILENAME ) String fileName )
//    {
//        LOG.warn( "upload called ..." );
//        LOG.info( "fileDetails = " + fileName );
//
//        // handle the upload of the war file to some path on the file system
//        String fileLocation = /* config.getWarUploadPath() + */ "target/" + fileName;
//        writeToFile( in, fileLocation );
//        return Response.status( Response.Status.CREATED ).entity( fileLocation ).build();
//    }

//    @SuppressWarnings( "unchecked" )
//    @POST
//    @Path( "/results" )
//    @Consumes( MediaType.MULTIPART_FORM_DATA )
//    @Produces( MediaType.TEXT_PLAIN )
//    public Response uploadResults(
//        @FormDataParam( CONTENT ) InputStream in,
//        @FormDataParam( RUN_ID ) String runId ) throws Exception
//    {
//        JSONObject object = ( JSONObject ) new JSONParser().parse( new InputStreamReader( in ) );
//        JSONArray runResults = ( JSONArray ) object.get( "runResults" );
//        Iterator<JSONObject> iterator = runResults.iterator();
//
//        //noinspection WhileLoopReplaceableByForEach
//        while( iterator.hasNext() ) {
//            JSONObject jsonResult = iterator.next();
//
//            BasicRunResult runResult = new BasicRunResult(
//                    runId,
//                    Util.getInt( jsonResult, "runCount" ),
//                    Util.getInt( jsonResult, "runTime" ),
//                    Util.getInt( jsonResult, "ignoreCount" ),
//                    Util.getInt( jsonResult, "failureCount" )
//            );
//
//            if ( runResultDao.save( runResult ) ) {
//                LOG.info( "Saved run result: {}", runResult );
//            }
//        }
//
//        return Response.status( Response.Status.CREATED ).entity( "TRUE" ).build();
//    }


//    @SuppressWarnings( "unchecked" )
//    @POST
//    @Path( "/summary" )
//    @Consumes( MediaType.MULTIPART_FORM_DATA )
//    @Produces( MediaType.TEXT_PLAIN )
//    public Response uploadSummary( @FormDataParam( CONTENT ) InputStream in,
//                                   @FormDataParam( RUNNER_HOSTNAME ) String runnerHostname ) throws Exception
//    {
//        JSONObject json = ( JSONObject ) new JSONParser().parse( new InputStreamReader( in ) );
//        BasicRun run = new BasicRun(
//                COMMIT_ID,
//                runnerHostname,
//                Util.getInt( json, "runNumber" ),
//                Util.getString( json, "testName" ) );
//        run.copyJson( json );
//
//        if ( runDao.save( run ) ) {
//            LOG.info( "Created new Run {} ", run );
//        }
//        else {
//            LOG.warn( "Failed to create new Run" );
//        }
//
//        return Response.status( Response.Status.CREATED ).entity( run.getId() ).build();
//    }


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
