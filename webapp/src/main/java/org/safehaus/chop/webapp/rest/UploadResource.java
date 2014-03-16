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
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;


/**
 * REST operation to upload (a.k.a. deploy) a project war file.
 */
@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path( UploadResource.ENDPOINT_URL )
public class UploadResource {
    public final static String ENDPOINT_URL = "/upload";
    private final static Logger LOG = LoggerFactory.getLogger( UploadResource.class );
    public static final String FILENAME_PARAM = "file";
    public static final String CONTENT = "content";


    @Inject
    RestFig config;


    @POST
    @Consumes( MediaType.MULTIPART_FORM_DATA )
    public Response upload(
            @FormDataParam( CONTENT ) InputStream in,
            @FormDataParam( FILENAME_PARAM ) String fileName )
    {
        LOG.warn( "upload called ..." );
        LOG.info( "fileDetails = " + fileName );

        // handle the upload of the war file to some path on the file system
        String fileLocation = /* config.getWarUploadPath() + */ "target/" + fileName;
        writeToFile( in, fileLocation );

        return Response.status( Response.Status.CREATED ).entity( fileLocation ).build();
    }


//    @POST
//    @Consumes( MediaType.MULTIPART_FORM_DATA )
//    public Response upload()
////            @FormDataParam( CONTENT ) FormDataBodyPart content )
////            @FormDataParam( CONTENT ) InputStream in,
////            @FormDataParam( FILENAME_PARAM ) String fileName )
//    {
////        FormDataContentDisposition cd = content.getFormDataContentDisposition();
////        InputStream in = content.getValueAs( InputStream.class );
////
////        LOG.warn( "upload called ..." );
////        LOG.info( "fileDetails = " + cd.getFileName() );
////
////        // handle the upload of the war file to some path on the file system
////        String fileLocation = /* config.getWarUploadPath() + */ cd.getFileName();
////        writeToFile( in, fileLocation );
//
//        return Response.status( Response.Status.CREATED ).entity( "ffooo" ).build();
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
