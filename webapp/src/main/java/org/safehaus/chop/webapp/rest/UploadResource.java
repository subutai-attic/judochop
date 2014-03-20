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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST operation to upload (a.k.a. deploy) a project war file.
 */
@Singleton
@Produces(MediaType.TEXT_PLAIN)
@Path(UploadResource.ENDPOINT_URL)
public class UploadResource {

    public final static String ENDPOINT_URL = "/upload";
    private final static Logger LOG = LoggerFactory.getLogger(UploadResource.class);
    public static final String FILENAME_PARAM = "file";
    public static final String CONTENT = "content";

    @Inject
    RestFig config;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(MimeMultipart multipart) {
        try {
            String filename = multipart.getBodyPart(0).getContent().toString();
            InputStream in = multipart.getBodyPart(1).getInputStream();
            String fileLocation = /* config.getWarUploadPath() + */ filename;
            writeToFile(in, fileLocation);
            return Response.status(Response.Status.CREATED).entity("foo").build();
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        } catch (MessagingException ex) {
            LOG.error(ex.getMessage());
        }
        return Response.status(Response.Status.CREATED).entity("foo").build();

    }

    private void writeToFile(InputStream in, String fileLocation) {
        OutputStream out = null;

        try {
            int read;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(fileLocation);

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            LOG.error("Failed to write out file: " + fileLocation, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.error("Failed while trying to close output stream for {}", fileLocation);
                }
            }
        }
    }
}
