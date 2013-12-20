package org.safehaus.chop.api.store.amazon;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.store.StoreOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


/** Used to encapsulate the various S3 operations to perform. */
public class S3Operations implements StoreOperations, ConfigKeys {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );


    private final AmazonS3Client client;
    private final DynamicStringProperty awsBucket;
    private final DynamicStringProperty gitUuid =
            DynamicPropertyFactory.getInstance().getStringProperty( GIT_UUID_KEY, "none" );


    @Inject
    public S3Operations( AmazonS3Client client, @Named( AWS_BUCKET_KEY ) DynamicStringProperty awsBucket ) {
        this.client = client;
        this.awsBucket = awsBucket;
    }


    /**
     * Registers this runner's instance by adding its instance information into S3 as a properties file into the bucket
     * using the following key format:
     *
     * "drivers/formationName-publicHostname.properties"
     *
     * @param publicHostname the runner's instance publicHostname
     */
    @Override
    public String getRunnerKey( String publicHostname ) {
        StringBuilder sb = new StringBuilder();
        sb.append( publicHostname ).append( ".properties" );
        return sb.toString();
    }


    /**
     * Registers this runner's instance by adding its instance information into S3 as a properties file into the bucket
     * using the following key format:
     *
     * "drivers/formationName-publicHostname.properties"
     *
     * @param runner the runner's instance metadata to be registered
     */
    @Override
    public void register( Runner runner ) {

        if ( runner == null || runner.getHostname() == null ) {
            LOG.warn( "Refusing to register null runner or one without a hostname." );
            return;
        }

        String blobName = getRunnerKey( runner.getHostname() );

        try {
            PutObjectRequest putRequest = new PutObjectRequest( awsBucket.get(), RUNNERS_PATH + "/" + blobName,
                    runner.getPropertiesAsStream(), new ObjectMetadata() );
            client.putObject( putRequest );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to create input stream for object.", e );
        }

        LOG.info( "Successfully registered {}", blobName );
    }


    /**
     * Scans tests under the bucket as "tests/.*\/test-info.json
     *
     * @return a set of keys as Strings for test information
     */
    @Override
    public Set<Project> getTests() throws IOException {
        Set<Project> tests = new HashSet<Project>();
        ObjectListing listing = client.listObjects( awsBucket.get(), CONFIGS_PATH + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                if ( key.startsWith( CONFIGS_PATH + "/" ) && key.endsWith( "/test-info.json" ) ) {
                    tests.add( getJsonObject( key, Project.class ) );
                }
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );

        return tests;
    }


    /**
     * Gets the runner instance information from S3 as a map of keys to their properties.
     *
     * @return the keys mapped to runner instance properties
     */
    @Override
    public Map<String, Runner> getRunners() {
        return getRunners( null );
    }


    /**
     * Gets the runner instance information from S3 as a map of keys to their properties.
     *
     * @param runner a runner to exclude from results (none if null)
     *
     * @return the keys mapped to runner instance properties
     */
    @Override
    public Map<String, Runner> getRunners( Runner runner ) {
        Map<String, Runner> runners = new HashMap<String, Runner>();
        ObjectListing listing = client.listObjects( awsBucket.get(), RUNNERS_PATH );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                LOG.debug( "Got key {} while scanning under drivers container", key );

                S3Object s3Object = client.getObject( awsBucket.get(), key );

                if ( runner != null && runner.getHostname() != null &&
                        s3Object.getKey().contains( runner.getHostname() ) ) {
                    continue;
                }

                try {
                    runners.put( key, new Ec2Runner( s3Object.getObjectContent() ) {} );
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


    /**
     * Downloads a blob in the S3 store by key, and places it in a temporary file returning the file. Use this to
     * download big things like war files or results.
     *
     * @param tempDir the temporary directory to use
     * @param key the blobs key
     *
     * @return the File object referencing the temporary file
     *
     * @throws IOException if there's a problem accessing the stream
     */
    @Override
    public File download( File tempDir, String key ) throws IOException {
        File tempFile = File.createTempFile( "download", "file", tempDir );
        LOG.debug( "Created temporary file {} for new war download.", tempFile.getAbsolutePath() );

        S3Object s3Object = client.getObject( awsBucket.get(), key );
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


    private <T> T getJsonObject( String key, Class<T> clazz ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        S3Object s3Object = client.getObject( awsBucket.get(), key );
        S3ObjectInputStream in = s3Object.getObjectContent();

        try {
            return mapper.readValue( in, clazz );
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


    @Override
    public <T> T putJsonObject( String key, Object obj ) {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream in = null;

        try {
            byte[] json = mapper.writeValueAsBytes( obj );
            in = new ByteArrayInputStream( json );
        }
        catch ( JsonProcessingException e ) {
            LOG.error( "Failed to serialize to JSON Project object {}", obj, e );
        }

        PutObjectRequest putRequest = new PutObjectRequest( awsBucket.get(), key, in, new ObjectMetadata() );
        client.putObject( putRequest );

        return null;
    }


    /**
     * Serializes a Summary object into Json and stores it in S3.
     *
     * @param project the test the Summary object is associated with
     * @param summary the Summary object to store in S3
     */
    @Override
    public void uploadRunInfo( Project project, ISummary summary ) {
        String loadKey = project.getLoadKey();

        if ( loadKey == null ) {
            LOG.error( "project.getLoadKey() was null. Abandoning summary upload." );
            return;
        }

        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey ).append( "results/" ).append( summary.getRunNumber() ).append( "/run-info.json" );

        String blobName = sb.toString();
        putJsonObject( blobName, summary );
        LOG.info( "Successfully registered {}", blobName );
    }


    /**
     * Uploads a file into S3 using the supplied key.
     *
     * @param key the supplied key
     * @param file the file to upload
     */
    @Override
    public void putFile( String key, File file ) {
        PutObjectRequest putRequest = null;
        try {
            putRequest =
                    new PutObjectRequest( awsBucket.get(), key, new FileInputStream( file ), new ObjectMetadata() );
        }
        catch ( FileNotFoundException e ) {
            LOG.error( "Failed to upload the results {} file", file, e );
        }

        client.putObject( putRequest );
        LOG.info( "Successfully put file {} into S3 with key {}", file, key );
    }


    /**
     * Uploads results to be archived in S3 for analysis later.
     *
     * @param metadata the metadata associated with the runner instance
     * @param project the test information associated with the test the results ran on
     * @param summary the run information to also upload into S3 besides the results
     * @param results the results to upload
     */
    @Override
    public void uploadInfoAndResults( Runner metadata, Project project, ISummary summary, File results ) {
        uploadRunInfo( project, summary );

        String loadKey = project.getLoadKey();

        if ( loadKey == null ) {
            LOG.error( "project.getLoadKey() was null. Abandoning info and results upload." );
            return;
        }

        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey ).append( "results/" ).append( summary.getRunNumber() ).append( '/' )
          .append( metadata.getHostname() ).append( "-results.log" );

        String blobName = sb.toString();
        putFile( blobName, results );
    }


    /**
     * Checks if a key is present in S3.
     *
     * @param key the key to be checked for
     *
     * @return true if it exists, false otherwise
     */
    private boolean hasKey( String key ) {
        ObjectListing listing = client.listObjects( awsBucket.get(), key );

        if ( listing.getObjectSummaries().isEmpty() ) {
            return false;
        }

        S3ObjectSummary summary = listing.getObjectSummaries().get( 0 );
        LOG.debug( "Got key {} while scanning for key {}", summary.getKey() );
        return true;
    }


    /**
     * Uploads the information associated with a test in a Project object into S3 as Json.
     *
     * @param project the Project object to be serialized and stored in S3
     */
    @Override
    public void uploadTestInfo( Project project ) {
        String loadKey = project.getLoadKey();

        if ( loadKey == null ) {
            LOG.error( "project.loadKey() was null. Abandoning upload." );
            return;
        }

        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey ).append( "results/test-info.json" );

        if ( hasKey( sb.toString() ) ) {
            LOG.warn( "The key {} already exists for Project - not updating!", sb.toString() );
            return;
        }

        String blobName = sb.toString();
        putJsonObject( blobName, project );
        LOG.info( "Successfully saved Project with key {}", blobName );
    }


    /**
     * Tries to load a Project file based on runner metadata prepackaged. If it cannot find it then null is returned.
     *
     * @return the Project object if it exists in the store or null if it does not
     */
    @Override
    public Project loadTestInfo() {
        String gitUuid = this.gitUuid.get();

        if ( gitUuid == null ) {
            LOG.error( "Could not find gitUuid: returning null Project." );
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append( CONFIGS_PATH ).append( '/' ).append( gitUuid ).append( "/test-info.json" );

        try {
            return getJsonObject( sb.toString(), Project.class );
        }
        catch ( Exception e ) {
            LOG.error( "Could not find test-info.json at {}: returning null Project", sb.toString(), e );
            return null;
        }
    }


    /**
     * Tries to load a Project file based on runner metadata prepackaged. If it cannot find it then null is returned.
     *
     * @return the Project object if it exists in the store or null if it does not
     */
    @Override
    public Project getTestInfo( String key ) {
        key = key.substring( 0, key.length() - "perftest.war".length() );

        try {
            return getJsonObject( key + "test-info.json", Project.class );
        }
        catch ( Exception e ) {
            LOG.error( "Could not find test-info.json at {}: returning null Project", key, e );
            return null;
        }
    }


    @Override
    public void deleteTests() {
        Set<Project> tests = new HashSet<Project>();
        ObjectListing listing = client.listObjects( awsBucket.get(), CONFIGS_PATH + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();
                client.deleteObject( awsBucket.get(), key );
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );
    }


    @Override
    public void deleteGhostRunners( Collection<String> activeRunners ) {
        Map<String, Runner> registeredRunners = getRunners();
        Runner runner;
        for( String key : registeredRunners.keySet() ) {
            runner = registeredRunners.get( key );
            if ( ! activeRunners.contains( runner.getHostname() ) ) {
                String path = RUNNERS_PATH + "/" + runner.getHostname() + ".properties";
                client.deleteObject( awsBucket.get(), path );
            }
        }
    }
}
