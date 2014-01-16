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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.ProjectBuilder;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;


/** Handles S3 interactions to interface with other test drivers. */
@Singleton
public class AmazonS3Store implements Store, Runnable, Constants {

    private final static Logger LOG = LoggerFactory.getLogger( AmazonS3Store.class );
    private static final String PRETTY_PRINT_RESULTS = "";

    private boolean started = false;
    private Runner me;
    private Map<String, Runner> runners = new HashMap<String, Runner>();
    private final Object lock = new Object();
    private final AmazonFig amazonFig;
    private final Injector injector;
    private AmazonS3Client client;
    private DynamicBooleanProperty prettyPrint;


    @Inject
    public AmazonS3Store( Injector injector ) {
        Preconditions.checkNotNull( injector );

        this.injector = injector;
        client = injector.getInstance( AmazonS3Client.class );

        // singleton should have bypasses added in ServletConfig for the EC2 environment
        me = injector.getInstance( Runner.class );
        amazonFig = injector.getInstance( AmazonFig.class );
        prettyPrint = DynamicPropertyFactory.getInstance().getBooleanProperty( PRETTY_PRINT_RESULTS, true );
    }



    @Override
    public void start() {
        Preconditions.checkState( ! started, "Doh! Must not be started to be started!" );

        synchronized ( lock ) {
            client = injector.getInstance( AmazonS3Client.class );

            if ( me.getHostname() != null ) {
                register( me );
            }

            started = true;
            runners = getRunners( me );
        }

        new Thread( this ).start();
    }


    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void stop() {
        synchronized ( lock ) {
            if ( isStarted() && client != null ) {
                try {
                    started = false;
                    lock.notifyAll();
                    client.shutdown();
                }
                catch ( Exception e ) {
                    LOG.warn( "Ran into issues shutting down the Amazon client: ", e.getMessage() );
                }
            }
        }
    }


    @Override
    public void store( final Project project, final Summary summary, final File resultsFile,
                       final Class<?> testClass ) {
        store( me, project, summary, resultsFile, testClass );
    }


    /**
     * Creates the key for a runner's properties file in the store.
     *
     * "$RUNNERS_PATH/publicHostname.properties"
     *
     * @param publicHostname the runner's publicHostname
     */
    private String getRunnerKey( String publicHostname ) {
        Preconditions.checkNotNull( publicHostname, "The publicHostname cannot be null." );
        StringBuilder sb = new StringBuilder();
        sb.append( RUNNERS_PATH ).append( '/' )
          .append( publicHostname ).append( ".properties" );
        return sb.toString();
    }


    @Override
    public Map<String, Runner> getRunners() {
        return getRunners( null );
    }


    @Override
    public Map<String, Runner> getRunners( Runner runner ) {
        Map<String, Runner> runners = new HashMap<String, Runner>();
        ObjectListing listing = client.listObjects( amazonFig.getAwsBucket(), RUNNERS_PATH );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                LOG.debug( "Got key {} while scanning under drivers container", key );

                S3Object s3Object = client.getObject( amazonFig.getAwsBucket(), key );

                if ( runner != null && runner.getHostname() != null &&
                        s3Object.getKey().contains( runner.getHostname() ) ) {
                    continue;
                }

                try {
                    Ec2RunnerBuilder builder = new Ec2RunnerBuilder( s3Object.getObjectContent() );
                    runners.put( key, builder.getRunner() );
                }
                catch ( IOException e ) {
                    LOG.error( "Failed to load metadata for runner {}", key, e );
                }
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );

        return runners;
    }


    @Override
    public void deleteGhostRunners( Set<String> activeRunners ) {
        Map<String, Runner> registeredRunners = getRunners();
        Runner runner;
        for( String key : registeredRunners.keySet() ) {
            runner = registeredRunners.get( key );
            if ( ! activeRunners.contains( runner.getHostname() ) ) {
                String path = getRunnerKey( runner.getHostname() );
                client.deleteObject( amazonFig.getAwsBucket(), path );
            }
        }
    }


    @Override
    public void register( Runner runner ) {
        Preconditions.checkNotNull( runner, "The runner cannot be null." );
        Preconditions.checkNotNull( runner.getHostname(), "The runners public hostname cannot be null." );

        String key = getRunnerKey( runner.getHostname() );

        try {
            PutObjectRequest putRequest = new PutObjectRequest( amazonFig.getAwsBucket(), key,
                    getPropertiesAsStream( runner ), new ObjectMetadata() );
            client.putObject( putRequest );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to create input stream for object.", e );
        }

        LOG.info( "Successfully registered {}", key );
    }


    @Override
    public void unregister( Runner runner ) {
        Preconditions.checkNotNull( runner );
        Preconditions.checkNotNull( runner, "The runner cannot be null." );
        Preconditions.checkNotNull( runner.getHostname(), "The runners public hostname cannot be null." );

        String key = getRunnerKey( runner.getHostname() );

        DeleteObjectRequest delRequest = new DeleteObjectRequest( amazonFig.getAwsBucket(), key );
        client.deleteObject( delRequest );
    }



    /**
     * Gets the properties listing as an input stream.
     *
     * @return the properties listing as an input stream
     *
     * @throws java.io.IOException there are io failures
     */
    private InputStream getPropertiesAsStream( Runner runner ) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Properties properties = new Properties();


        if ( runner.getUrl() != null ) {
            properties.setProperty( Runner.URL_KEY, runner.getUrl() );
        }

        if ( runner.getTempDir() != null ) {
            properties.setProperty( Runner.RUNNER_TEMP_DIR_KEY, runner.getTempDir() );
        }

        properties.setProperty( Runner.SERVER_PORT_KEY, String.valueOf( runner.getServerPort() ) );

        if ( runner.getIpv4Address() != null ) {
            properties.setProperty( Runner.IPV4_KEY, runner.getIpv4Address() );
        }

        if ( runner.getHostname() != null ) {
            properties.setProperty( Runner.HOSTNAME_KEY, runner.getHostname() );
        }

        properties.store( bytes, null );
        bytes.flush();
        return new ByteArrayInputStream( bytes.toByteArray() );
    }


    @Override
    public Set<Project> getProjects() throws IOException {
        Set<Project> tests = new HashSet<Project>();
        ObjectListing listing = client.listObjects( amazonFig.getAwsBucket(), TESTS_PATH + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                if ( key.startsWith( TESTS_PATH + "/" ) && key.endsWith( "/" + PROJECT_FILE ) ) {
                    tests.add( getFromProperties( key ) );
                }
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );

        return tests;
    }


    @Override
    public void store( Project project ) {
        String loadKey = ChopUtils.getTestBase( project );
        StringBuilder sb = new StringBuilder();
        sb.append( loadKey ).append( PROJECT_FILE );

        if ( hasKey( sb.toString() ) ) {
            LOG.warn( "The key {} already exists for Project - not updating!", sb.toString() );
            return;
        }

        String blobName = sb.toString();
        putJsonObject( blobName, project );
        LOG.info( "Successfully saved Project with key {}", blobName );
    }


    @Override
    public Project getProject( String loadKey ) {
        Preconditions.checkNotNull( loadKey, "The key cannot be null" );

        loadKey = ChopUtils.getTestBase( loadKey );

        try {
            return getFromProperties( loadKey + PROJECT_FILE );
        }
        catch ( Exception e ) {
            LOG.error( "Could not find project file at {}: returning null Project", loadKey, e );
            return null;
        }
    }


    @Override
    public void deleteProjects() {
        ObjectListing listing = client.listObjects( amazonFig.getAwsBucket(), TESTS_PATH + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();
                client.deleteObject( amazonFig.getAwsBucket(), key );
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );
    }


    @Override
    public int getNextRunNumber( Project project ) {
        String loadKey = ChopUtils.getTestBase( project );

        int runNumber = 1;
        StringBuilder sb = new StringBuilder();

        do {
            sb.setLength( 0 );
            String prefix = sb.append( loadKey ).append( runNumber ).toString();

            try {
                ObjectListing listing = client.listObjects( amazonFig.getAwsBucket(), prefix );

                if ( listing.getObjectSummaries().size() > 0 ) {
                    // entries for the current runNumber exist so let's advance to the next candidate
                    runNumber++;
                }
                else {
                    /*
                     * Entries for the current runNumber do not exist, so we return it as the next available
                     * runNumber.
                     *
                     * I'm not certain if listObjects will blow chunks when no keys are found
                     * with the common prefix. For a non existent runNumber (our candidate to return)
                     * the supplied prefix should not exist. Just in case we also return the current
                     * runNumber in the catch block below.
                     */
                    return runNumber;
                }
            }
            catch ( Exception e ) {
                return runNumber;
            }
        }
        while ( true );
    }


    @Override
    public boolean hasCompleted( Runner runner, Project project, int runNumber, Class<?> testClass ) {
        Preconditions.checkNotNull( runner, "The runner argument cannot be null." );
        Preconditions.checkNotNull( testClass, "The testClass argument cannot be null." );

        String loadKey = ChopUtils.getTestBase( project );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
          .append( runNumber ).append( '/' )
          .append( testClass.getName() ).append( '/' )
          .append( runner.getHostname() ).append( SUMMARY_SUFFIX );
        String blobName = sb.toString();

        try {
            S3Object object = client.getObject( amazonFig.getAwsBucket(), blobName );
            object.getObjectContent().close();
            return true;
        }
        catch ( Exception e ) {
            return false;
        }
    }


    /**
     * Serializes a Summary object into Json and stores it.
     *
     * @param project the test the Summary object is associated with
     * @param summary the Summary object to store
     */
    private void store( Runner metadata, Project project, Summary summary, Class<?> testClass ) {
        Preconditions.checkNotNull( summary, "The summary argument cannot be null." );

        String loadKey = ChopUtils.getTestBase( project );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
          .append( summary.getRunNumber() ).append( '/' )
          .append( testClass.getName() ).append( '/' )
          .append( metadata.getHostname() ).append( SUMMARY_SUFFIX );

        String blobName = sb.toString();
        LOG.info( "Saving summary json with key = {}", blobName );
        putJsonObject( blobName, summary );
        LOG.info( "Successfully registered {}", blobName );
    }


    /**
     * Stores the summary and detailed results for analysis later.
     *
     * @param metadata the metadata associated with the runner instance
     * @param project the project test information
     * @param summary the run summary to also store
     * @param results the detailed results to store
     */
    private void store( Runner metadata, Project project, Summary summary, File results, Class<?> testClass ) {
        store( metadata, project, summary, testClass );

        String loadKey = ChopUtils.getTestBase( project );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
          .append( summary.getRunNumber() ).append( '/' )
          .append( testClass.getName() ).append( '/' )
          .append( metadata.getHostname() ).append( RESULTS_SUFFIX );

        putFile( sb.toString(), results );
    }


    /**
     * Checks if a key is present in the store.
     *
     * @param key the key to be checked for
     *
     * @return true if it exists, false otherwise
     */
    private boolean hasKey( String key ) {
        ObjectListing listing = client.listObjects( amazonFig.getAwsBucket(), key );

        if ( listing.getObjectSummaries().isEmpty() ) {
            return false;
        }

        S3ObjectSummary summary = listing.getObjectSummaries().get( 0 );
        LOG.debug( "Got key {} while scanning for key {}", summary.getKey() );
        return true;
    }


    @Override
    public void download( File resultsDirectory, String prefix, FilenameFilter filter ) throws Exception {
        Preconditions.checkNotNull( resultsDirectory, "resultsDirectory cannot be null" );

        ObjectListing listing;

        if ( prefix == null ) {
            listing = client.listObjects( amazonFig.getAwsBucket() );
        }
        else {
            listing = client.listObjects( amazonFig.getAwsBucket(), prefix );
        }

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();
                File file = new File( new File( resultsDirectory, amazonFig.getAwsBucket() ), key );

                if ( key.startsWith( prefix ) && filter.accept( file.getParentFile(), key ) ) {
                    S3Object object = client.getObject( amazonFig.getAwsBucket(),
                            key.substring( key.lastIndexOf( '/' ) ) );
                    LOG.debug( "Downloading candidate with key {}", key );

                    S3ObjectInputStream in = object.getObjectContent();

                    if ( ! file.getParentFile().mkdirs() ) {
                        LOG.warn( "Failed to create parent directory {} for {}", file.getParent(), key );
                    }

                    FileOutputStream out = new FileOutputStream( file );
                    byte[] buffer = new byte[1024];
                    int readAmount;

                    while ( ( readAmount = in.read( buffer ) ) != -1 ) {
                        out.write( buffer, 0, readAmount );
                    }

                    out.flush();
                    out.close();
                    in.close();
                    LOG.info( "Successfully downloaded {} from S3 to {}.", key, file.getAbsoluteFile() );
                }
            }
        }
        while ( listing.isTruncated() );
    }


    @Override
    public File download( File tempDir, String key ) throws IOException {
        File tempFile = File.createTempFile( "download", "file", tempDir );
        LOG.debug( "Created temporary file {} for new war download.", tempFile.getAbsolutePath() );

        S3Object s3Object = client.getObject( amazonFig.getAwsBucket(), key );
        LOG.debug( "Got S3Object:\n{}", s3Object.toString() );

        // Download war file contents into temporary file
        S3ObjectInputStream in = s3Object.getObjectContent();
        FileOutputStream out = new FileOutputStream( tempFile );
        byte[] buffer = new byte[1024];
        int readAmount;

        while ( ( readAmount = in.read( buffer ) ) != -1 ) {
            out.write( buffer, 0, readAmount );
        }

        out.flush();
        out.close();
        in.close();
        LOG.info( "Successfully downloaded {} from S3 to {}.", key, tempFile.getAbsoluteFile() );
        return tempFile;
    }


    private Project getFromProperties( String key ) throws IOException {
        S3Object s3Object = client.getObject( amazonFig.getAwsBucket(), key );
        S3ObjectInputStream in = s3Object.getObjectContent();

        try {
            Properties props = new Properties();
            props.load( in );
            in.close();

            ProjectBuilder builder = new ProjectBuilder( props );
            return builder.getProject();
        }
        catch ( IOException e ) {
            LOG.error( "Failed to marshall {} into a valid object.", key, e );
            throw e;
        }
        finally {
            if ( in != null ) {
                in.close();
            }
        }
    }


    private <T> T putJsonObject( String key, Object obj ) {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream in = null;

        try {
            byte[] json;
            if ( prettyPrint.get() ) {
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                json = writer.writeValueAsBytes( obj );
            }
            else {
                json = mapper.writeValueAsBytes( obj );
            }

            in = new ByteArrayInputStream( json );
        }
        catch ( JsonProcessingException e ) {
            LOG.error( "Failed to serialize to JSON Project object {}", obj, e );
        }

        PutObjectRequest putRequest = new PutObjectRequest( amazonFig.getAwsBucket(), key, in, new ObjectMetadata() );
        client.putObject( putRequest );

        return null;
    }


    /**
     * Uploads a file using the supplied key.
     *
     * @param key the supplied key
     * @param file the file to upload
     */
    private void putFile( String key, File file ) {
        PutObjectRequest putRequest = null;
        try {
            putRequest =
                    new PutObjectRequest( amazonFig.getAwsBucket(), key, new FileInputStream( file ), new ObjectMetadata() );
        }
        catch ( FileNotFoundException e ) {
            LOG.error( "Failed to upload the results {} file", file, e );
        }

        client.putObject( putRequest );
        LOG.info( "Successfully put file {} into store with key {}", file, key );
    }


    @Override
    public void run() {
        while ( started ) {
            try {
                synchronized ( lock ) {
                    // wait first since we scan on start()
                    lock.wait( amazonFig.getScanPeriod() );

                    if ( started ) {
                        runners = getRunners( me );
                    }

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
