package org.safehaus.perftest.amazon;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.safehaus.perftest.settings.PropSettings;
import org.safehaus.perftest.settings.RunInfo;
import org.safehaus.perftest.settings.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


/**
 * Used to encapsulate the various S3 operations to perform.
 */
public class S3Operations {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );
    private final AmazonS3Client client;


    @Inject
    public S3Operations( AmazonS3Client client ) {
        this.client = client;
    }


    /**
     * Registers this runner's instance by adding it's instance information into
     * S3 as a properties file into the bucket using the following key format:
     *
     *      "runners/formationName-publicHostname.properties"
     *
     * @param publicHostname the runner's instance publicHostname
     */
    public String getRunnerKey( String publicHostname ) {
        StringBuilder sb = new StringBuilder();
        sb.append( PropSettings.getFormation() ).append('-')
          .append( publicHostname ).append( ".properties" );
        return sb.toString();
    }



    /**
     * Registers this runner's instance by adding it's instance information into
     * S3 as a properties file into the bucket using the following key format:
     *
     *      "runners/formationName-publicHostname.properties"
     *
     * @param metadata the runner's instance metadata to be registered
     */
    public void register( Ec2Metadata metadata ) {
        String blobName = getRunnerKey( metadata.getPublicHostname() );

        try {
            PutObjectRequest putRequest = new PutObjectRequest( PropSettings.getBucket(),
                    PropSettings.getRunners() + "/" + blobName,
                    metadata.getPropertiesAsStream(), new ObjectMetadata() );
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
    public Set<String> getTests() {
        Set<String> tests = new HashSet<String>();
        ObjectListing listing = client.listObjects( PropSettings.getBucket(), PropSettings.getTests() + "/" );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                if ( key.startsWith( PropSettings.getTests() + "/" ) && key.endsWith( "/test-info.json" ) )
                {
                    continue;
                }

                tests.add( summary.getKey() );
            }

            listing = client.listNextBatchOfObjects( listing );
        }
        while ( listing.isTruncated() );

        return tests;
    }


    /**
     * Gets the runner instance information from S3 as a map of keys to their properties.
     *
     * @param formation the formation to get the runners for
     * @return the keys mapped to runner instance properties
     */
    public Map<String,Ec2Metadata> getRunners( String formation ) {
        Map<String,Ec2Metadata> runners = new HashMap<String, Ec2Metadata>();
        ObjectListing listing = client.listObjects( PropSettings.getBucket(),
                PropSettings.getRunners() + "/" + formation );

        do {
            for ( S3ObjectSummary summary : listing.getObjectSummaries() ) {
                String key = summary.getKey();

                LOG.debug( "Got key {} while scanning under runners container", key );

                S3Object s3Object = client.getObject( PropSettings.getBucket(), key );

                try {
                    runners.put( key, new Ec2Metadata( s3Object.getObjectContent() ) );
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
     * Downloads a blob in the S3 store by key, and places it in a temporary file returning the file.
     * Use this to download big things like war files or results.
     *
     * @param tempDir the temporary directory to use
     * @param key the blobs key
     * @return the File object referencing the temporary file
     * @throws IOException if there's a problem accessing the stream
     */
    public File download( File tempDir, String key ) throws IOException {
        File tempFile = File.createTempFile( "download", "file", tempDir );
        LOG.debug( "Created temporary file {} for new war download.", tempFile.getAbsolutePath() );

        S3Object s3Object = client.getObject( PropSettings.getBucket(), key );
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


    public <T> T putJsonObject( String key, Object obj )
    {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream in = null;

        try {
            byte[] json = mapper.writeValueAsBytes( obj );
            in = new ByteArrayInputStream( json );
        }
        catch ( JsonProcessingException e ) {
            LOG.error("Failed to serialize to JSON TestInfo object {}", obj, e);
        }

        PutObjectRequest putRequest = new PutObjectRequest( PropSettings.getBucket(),
                key, in, new ObjectMetadata() );
        client.putObject( putRequest );

        return null;
    }


    /**
     * Serializes a RunInfo object into Json and stores it in S3.
     *
     * @param testInfo the test the RunInfo object is associated with
     * @param runInfo the RunInfo object to store in S3
     */
    public void uploadRunInfo( TestInfo testInfo, RunInfo runInfo ) {
        String loadKey = testInfo.getLoadKey();
        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
                .append( "results/" )
                .append( runInfo.getRunNumber() )
                .append( "/run-info.json" );

        String blobName = sb.toString();
        putJsonObject( blobName, runInfo );
        LOG.info( "Successfully registered {}", blobName );
    }


    /**
     * Uploads a file into S3 using the supplied key.
     *
     * @param key the supplied key
     * @param file the file to upload
     */
    public void putFile( String key, File file ) {
        PutObjectRequest putRequest = null;
        try {
            putRequest = new PutObjectRequest( PropSettings.getBucket(),
                    key, new FileInputStream( file ), new ObjectMetadata() );
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
     * @param testInfo the test information associated with the test the results ran on
     * @param runInfo the run information to also upload into S3 besides the results
     * @param results the results to upload
     */
    public void uploadInfoAndResults( Ec2Metadata metadata, TestInfo testInfo, RunInfo runInfo, File results ) {
        uploadRunInfo( testInfo, runInfo );

        String loadKey = testInfo.getLoadKey();
        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
                .append( "results/" )
                .append( runInfo.getRunNumber() )
                .append( '/' )
                .append( metadata.getPublicHostname() )
                .append( "-results.log" );

        String blobName = sb.toString();
        putFile( blobName, results );
    }


    /**
     * Checks if a key is present in S3.
     *
     * @param key the key to be checked for
     * @return true if it exists, false otherwise
     */
    private boolean hasKey( String key ) {
        ObjectListing listing = client.listObjects( PropSettings.getBucket(), key );

        if ( listing.getObjectSummaries().isEmpty() ) {
            return false;
        }

        S3ObjectSummary summary = listing.getObjectSummaries().get( 0 );
        LOG.debug( "Got key {} while scanning for key {}", summary.getKey() );
        return true;
    }


    /**
     * Uploads the information associated with a test in a TestInfo object into S3 as Json.
     *
     * @param testInfo the TestInfo object to be serialized and stored in S3
     */
    public void uploadTestInfo( TestInfo testInfo ) {
        String loadKey = testInfo.getLoadKey();
        loadKey = loadKey.substring( 0, loadKey.length() - "perftest.war".length() );

        StringBuilder sb = new StringBuilder();
        sb.append( loadKey )
                .append( "results/" )
                .append( "test-info.json" );

        if ( hasKey( sb.toString() ) ) {
            LOG.warn( "The key {} already exists for TestInfo - not updating!", sb.toString() );
            return;
        }

        String blobName = sb.toString();
        putJsonObject( blobName, testInfo );
        LOG.info( "Successfully saved TestInfo with key {}", blobName );
    }
}
