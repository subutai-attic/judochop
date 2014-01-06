package org.safehaus.chop.api;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/** The S3 Service is used to register the node so other nodes in the same perftest formation can access it. */
public interface StoreService {

    void start();

    boolean isStarted();

    void stop();

    void triggerScan();

    Set<String> listRunners();

    Runner getRunner( String key );

    Map<String, Runner> getRunners();

    Runner getMyMetadata();

    File download( File tempDir, String perftest ) throws Exception;

    void store( ProjectFig project, ISummary summary, File resultsFile );

    void store( ProjectFig project );

    void store( Runner metadata, ProjectFig project, ISummary summary, File results );

    void putFile( String key, File file );

    ProjectFig getProject( String testKey );

    String getRunnerKey( String publicHostname );

    void register( Runner metadata );

    Set<ProjectFig> getProjects() throws IOException;

    Map<String, Runner> getRunners( Runner runner );

    <T> T putJsonObject( String key, Object obj );

    void store( Runner metadata, ProjectFig project, ISummary summary );

    void deleteProjects();

    void deleteGhostRunners( Collection<String> activeRunners );
}
