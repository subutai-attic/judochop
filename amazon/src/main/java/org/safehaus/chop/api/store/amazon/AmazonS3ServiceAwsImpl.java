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
package org.safehaus.chop.api.store.amazon;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.store.StoreOperations;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.netflix.config.DynamicLongProperty;


/** Handles S3 interactions to interface with other test drivers. */
@Singleton
public class AmazonS3ServiceAwsImpl implements StoreService, Runnable, ConfigKeys {

    private final static Logger LOG = LoggerFactory.getLogger( AmazonS3ServiceAwsImpl.class );


    private boolean started = false;
    private StoreOperations operations;
    private Ec2Runner metadata;
    private Map<String, Runner> runners = new HashMap<String, Runner>();
    private final Object lock = new Object();
    private final AmazonS3Client client;
    private final DynamicLongProperty scanPeriod;


    @Inject
    public AmazonS3ServiceAwsImpl( AmazonS3Client client, Ec2Runner metadata, S3Operations operations,
                                   @Named( SCAN_PERIOD_KEY ) DynamicLongProperty scanPeriod ) {
        this.client = client;
        this.metadata = metadata;
        this.operations = operations;
        this.scanPeriod = scanPeriod;
    }


    @Override
    public void start() {
        if ( metadata.getHostname() != null ) {
            operations.register( metadata );
        }

        started = true;
        runners = operations.getRunners( metadata );
        new Thread( this ).start();
    }


    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void stop() {
        if ( isStarted() && client != null ) {
            client.shutdown();
        }
    }


    @Override
    public void triggerScan() {
        synchronized ( lock ) {
            lock.notifyAll();
        }
    }


    @Override
    public Set<String> listRunners() {
        return runners.keySet();
    }


    @Override
    public Runner getRunner( String key ) {
        return runners.get( key );
    }


    @Override
    public Map<String, Runner> getRunners() {
        return runners;
    }


    @Override
    public Runner getMyMetadata() {
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
    public void store( final Project project, final ISummary summary, final File resultsFile ) {
        operations.store( metadata, project, summary, resultsFile );
    }


    @Override
    public void store( final Project project ) {
        operations.store( project );
    }


    @Override
    public Project getProject() {
        return operations.getProject();
    }


    @Override
    public Set<Project> getProjects() throws IOException {
        return operations.getProjects();
    }


    public void run() {
        while ( started ) {
            try {
                synchronized ( lock ) {
                    // wait first since we scan on start()
                    lock.wait( scanPeriod.get() );

                    runners = operations.getRunners( metadata );

                    LOG.info( "Runners updated" );
                    for ( String runner : runners.keySet() ) {
                        LOG.info( "Found runner: {}", runner );
                    }
                }
            }
            catch ( InterruptedException e ) {
                LOG.warn( "S3 bucket scanner thread interrupted while sleeping.", e );
            }
        }
    }
}
