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
package org.safehaus.perftest.api.store.amazon;


import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.config.DynamicLongProperty;

import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.TestInfo;

import org.safehaus.perftest.api.store.StoreOperations;
import org.safehaus.perftest.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Handles S3 interactions to interface with other test runners.
 */
@Singleton
public class AmazonS3ServiceAwsImpl implements StoreService, Runnable, ConfigKeys {

    private final static Logger LOG = LoggerFactory.getLogger( AmazonS3ServiceAwsImpl.class );


    private boolean started = false;
    private StoreOperations operations;
    private Ec2RunnerInfo metadata;
    private Map<String, RunnerInfo> runners = new HashMap<String, RunnerInfo>();
    private final Object lock = new Object();
    private final AmazonS3Client client;
    private final DynamicLongProperty scanPeriod;


    @Inject
    public AmazonS3ServiceAwsImpl( AmazonS3Client client, Ec2RunnerInfo metadata, S3Operations operations,
                                   @Named( SCAN_PERIOD_KEY ) DynamicLongProperty scanPeriod ) {
        this.client = client;
        this.metadata = metadata;
        this.operations = operations;
        this.scanPeriod = scanPeriod;
    }


    @Override
    public void start() {
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
    public Set<TestInfo> listTests() throws IOException {
        return operations.getTests();
    }


    public void run() {
        while ( started ) {
            try {
                synchronized ( lock ) {
                    runners = operations.getRunners( metadata );

                    LOG.info( "Runners updated" );
                    for ( String runner : runners.keySet() )
                    {
                        LOG.info( "Found runner: {}", runner );
                    }

                    lock.wait( scanPeriod.get() );
                }
            }
            catch ( InterruptedException e ) {
                LOG.warn( "S3 bucket scanner thread interrupted while sleeping.", e );
            }
        }
    }
}
