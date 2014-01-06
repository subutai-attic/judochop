package org.safehaus.chop.api.store.amazon;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.ProjectFig;
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
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


/** Used to encapsulate the various store operations to perform. */
public class S3Operations implements StoreOperations, ConfigKeys {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );


    private final AmazonS3Client client;
    private final DynamicStringProperty awsBucket;
    private final DynamicStringProperty gitUuid =
            DynamicPropertyFactory.getInstance().getStringProperty( ProjectFig.GIT_UUID_KEY, "none" );


    @Inject
    public S3Operations( AmazonS3Client client, @Named( AWS_BUCKET_KEY ) DynamicStringProperty awsBucket ) {
        this.client = client;
        this.awsBucket = awsBucket;
    }


    /**
     * Creates the key for a runner's properties file in the store.
     *
     * "$RUNNERS_PATH/publicHostname.properties"
     *
     * @param publicHostname the runner's publicHostname
     */
    @Override
    public String getRunnerKey( String publicHostname ) {
        Preconditions.checkNotNull( publicHostname, "The publicHostname cannot be null." );
        StringBuilder sb = new StringBuilder();
        sb.append( RUNNERS_PATH ).append( '/' )
          .append( publicHostname ).append( ".properties" );
        return sb.toString();
    }


    /**
     * Gets the runner instance information from the store as a map of their keys to
     * their Runner information.
     *
     * @return the keys mapped to Runner information
     */
    @Override
    public Map<String, Runner> getRunners() {
        return getRunners( null );
    }


    /**
     * Gets the runner instance information from the store as a map of keys Runner instances.
     *
     * @param runner a runner to exclude from results (none if null)
     *
     * @return the keys mapped to Runner instance
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
                    Ec2RunnerBuilder builder = new Ec2RunnerBuilder( s3Object.getObjectContent() );
                    runners.put( key,  builder.getRunner() );
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


    /**
     * Registers this runner's instance by adding its instance information into the
     * store as a properties file into the bucket using the following key format:
     *
     * "$RUNNERS_PATH/publicHostname.properties"
     *
     * @param runner the runner's instance metadata to be registered
     */
    @Override
    public void register( Runner runner ) {
        Preconditions.checkNotNull( runner, "The runner cannot be null." );
        Preconditions.checkNotNull( runner.getHostname(), "The runners public hostname cannot be null." );

        String key = getRunnerKey( runner.getHostname() );

        try {
            PutObjectRequest putRequest = new PutObjectRequest( awsBucket.get(), key,
                    getPropertiesAsStream( runner ), new ObjectMetadata() );
            client.putObject( putRequest );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to create input stream for object.", e );
        }

        LOG.info( "Successfully registered {}", key );
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
        properties.setProperty( Runner.RUNNER_TEMP_DIR_KEY, runner.getTempDir() );
        properties.setProperty( Runner.URL_KEY, runner.getUrl() );
        properties.setProperty( Runner.SERVER_PORT_KEY, String.valueOf( runner.getServerPort() ) );
        properties.setProperty( Runner.HOSTNAME_KEY, runner.getHostname() );
        properties.setProperty( Runner.IPV4_KEY, runner.getIpv4Address() );
        properties.store( bytes, null );
        bytes.flush();
        return new ByteArrayInputStream( bytes.toByteArray() );
    }



    /**
     * Tries to load a Project file based on prepackaged Runner metadata. If it cannot
     * find the project then null is returned.
     *
     * @return the Project object if it exists in the store or null if it does not
     */
    @Override
    public ProjectFig getProject() {
        Preconditions.checkNotNull( gitUuid, "gitUuid cannot be null" );
        String uuid = gitUuid.get();
        Preconditions.checkNotNull( uuid, "gitUuid cannot be null" );

        // take half of chars from the front and half from the back
        uuid = uuid.substring( 0, CHARS_OF_UUID/2 ) + uuid.substring( uuid.length() - CHARS_OF_UUID/2 );

        StringBuilder sb = new StringBuilder();
        sb.append( CONFIGS_PATH ).append( '/' )
          .append( uuid ).append( "/" )
          .append( PROJECT_FILE );

        try {
            return getJsonObject( sb.toString(), ProjectFig.class );
        }
        catch ( Exception e ) {
            LOG.error( "Could not find project file at {}: returning null Project", sb.toString(), e );
            return null;
        }
    }


    /**
     * Scans for projects with test information under the bucket as:
     * </p>
     * "$CONFIGS_PATH/.*\/$PROJECT_FILE
     *
     * @return a set of keys as Strings for test information
     */
    @Override
    public Set<ProjectFig> getProjects() throws IOException {
        Set<ProjectFig> tests = new HashSet<ProjectFig>();
        ObjectListing listing = client.listObjects( awsBucket.get(), CONFIGS_PATH + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                if ( key.startsWith( CONFIGS_PATH + "/" ) && key.endsWith( "/" + PROJECT_FILE ) ) {
                    tests.add( getJsonObject( key, ProjectFig.class ) );
                }
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );

        return tests;
    }


    /**
     * Stores the project test information.
     *
     * @param project the Project object to be serialized and stored
     */
    @Override
    public void store( ProjectFig project ) {
        Preconditions.checkNotNull( project, "The project cannot be null." );
        Preconditions.checkNotNull( project.getLoadKey(), "The project load key cannot be null." );

        String loadKey = project.getLoadKey();
        loadKey = loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );

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


    /**
     * Tries to load a Project file based on prepackaged runner metadata: the runner's
     * loadKey. If it cannot find it, null is returned.
     *
     * @param loadKey the load key for the runner war
     * @return the Project object if it exists in the store or null if it does not
     */
    @Override
    public ProjectFig getProject( String loadKey ) {
        Preconditions.checkNotNull( loadKey, "The key cannot be null" );

        loadKey = loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );

        try {
            return getJsonObject( loadKey + PROJECT_FILE, ProjectFig.class );
        }
        catch ( Exception e ) {
            LOG.error( "Could not find project file at {}: returning null Project", loadKey, e );
            return null;
        }
    }


    @Override
    public void deleteProjects() {
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


    /**
     * Serializes a Summary object into Json and stores it.
     *
     * @param project the test the Summary object is associated with
     * @param summary the Summary object to store
     */
    @Override
    public void store( Runner metadata, ProjectFig project, ISummary summary ) {
        Preconditions.checkNotNull( project, "The project argument cannot be null." );
        Preconditions.checkNotNull( summary, "The summary argument cannot be null." );
        Preconditions.checkNotNull( project.getLoadKey(), "The project must have a valid load key." );

        String loadKey = project.getLoadKey();
        LOG.info( "Using loadKey = {}", loadKey );
        loadKey = loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );
        LOG.info( "Stripped loadKey to {}", loadKey );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
          .append( summary.getRunNumber() ).append( "/" )
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
    @Override
    public void store( Runner metadata, ProjectFig project, ISummary summary, File results ) {
        store( metadata, project, summary );

        String loadKey = project.getLoadKey();
        loadKey = loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
          .append( summary.getRunNumber() ).append( '/' )
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
        ObjectListing listing = client.listObjects( awsBucket.get(), key );

        if ( listing.getObjectSummaries().isEmpty() ) {
            return false;
        }

        S3ObjectSummary summary = listing.getObjectSummaries().get( 0 );
        LOG.debug( "Got key {} while scanning for key {}", summary.getKey() );
        return true;
    }


    /**
     * Downloads a file from the store by key, and places it in a temporary file returning
     * the file. Use this to download big things like war files or results.
     *
     * @param tempDir the temporary directory to use
     * @param key the blobs key
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
     * Uploads a file using the supplied key.
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
        LOG.info( "Successfully put file {} into store with key {}", file, key );
    }
}
