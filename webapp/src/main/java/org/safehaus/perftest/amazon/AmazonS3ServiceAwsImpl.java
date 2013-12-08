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
package org.safehaus.perftest.amazon;


import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.safehaus.perftest.RunnerInfo;
import org.safehaus.perftest.settings.PropSettings;
import org.safehaus.perftest.settings.Props;
import org.safehaus.perftest.RunInfo;
import org.safehaus.perftest.TestInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.*;


/**
 * Handles S3 interactions to interface with other test runners.
 */
@Singleton
public class AmazonS3ServiceAwsImpl implements AmazonS3Service, Runnable, Props {

    private final static Logger LOG = LoggerFactory.getLogger( AmazonS3ServiceAwsImpl.class );


    private boolean started = false;
    private S3Operations operations;
    private Ec2RunnerInfo metadata;
    private Map<String, RunnerInfo> runners = new HashMap<String, RunnerInfo>();
    private final Object lock = new Object();
    private final AmazonS3Client client;


    @Inject
    public AmazonS3ServiceAwsImpl( AmazonS3Client client, Ec2RunnerInfo metadata ) {
        this.client = client;

        this.metadata = metadata;
        operations = new S3Operations( client );
        metadata.setFormation( PropSettings.getFormation() );
    }


    @Override
    public void start() {
        System.setProperty( ARCHAIUS_CONTAINER_KEY, PropSettings.getBucket() );
        operations.register( metadata );
        started = true;
        new Thread( this ).start();
    }


    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void stop() {
        if ( isStarted() && client != null )
        {
            client.shutdown();
        }
    }


    @Override
    public void triggerScan()
    {
        synchronized ( lock )
        {
            lock.notifyAll();
        }
    }


    @Override
    public Set<String> listRunners()
    {
        return runners.keySet();
    }


    @Override
    public RunnerInfo getRunner( String key )
    {
        return runners.get( key );
    }


    @Override
    public Map<String, RunnerInfo> getRunners() {
        return runners;
    }


    @Override
    public void setServletContext( ServletContext context ) {
        metadata.setProperty( CONTEXT_PATH, context.getContextPath() );
        metadata.setProperty( SERVER_INFO_KEY, context.getServerInfo() );
        metadata.setProperty( SERVER_PORT_KEY, Integer.toString( PropSettings.getServerPort() ) );
        metadata.setProperty( CONTEXT_TEMPDIR_KEY, ( ( File ) context.getAttribute( CONTEXT_TEMPDIR_KEY ) ).getAbsolutePath() );
    }


    @Override
    public RunnerInfo getMyMetadata() {
        return metadata;
    }


    @Override
    public File download( File tempDir, String perftest ) throws Exception {
        try {
            return operations.download( tempDir, perftest );
        }
        catch ( Exception e ) {
            LOG.error( "Failed to execute load operation for {}", perftest, e );
            throw e;
        }
    }


    @Override
    public void uploadResults( final TestInfo testInfo, final RunInfo runInfo, final File resultsFile ) {
        operations.uploadInfoAndResults( metadata, testInfo, runInfo, resultsFile );
    }


    @Override
    public void uploadTestInfo( final TestInfo testInfo ) {
        operations.uploadTestInfo( testInfo );
    }


    @Override
    public Set<String> listTests()
    {
        return operations.getTests();
    }


    public void run() {
        while ( started ) {
            try {
                synchronized ( lock ) {
                    runners = operations.getRunners( metadata.getHostname() );

                    LOG.info( "Runners updated" );
                    for ( String runner : runners.keySet() )
                    {
                        LOG.info( "Found runner: {}", runner );
                    }

                    lock.wait( PropSettings.getScanPeriod() );
                }
            }
            catch ( InterruptedException e ) {
                LOG.warn( "S3 bucket scanner thread interrupted while sleeping.", e );
            }
        }
    }
}
