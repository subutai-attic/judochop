package org.safehaus.chop.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;


/** Operations performed against a perftest store. */
public interface StoreOperations {
    String getRunnerKey( String publicHostname );

    void register( Runner metadata );

    Set<Project> getTests() throws IOException;

    Map<String, Runner> getRunners( Runner runner );

    Map<String, Runner> getRunners();

    File download( File tempDir, String key ) throws IOException;

    <T> T putJsonObject( String key, Object obj );

    void uploadRunInfo( Project project, ISummary summary );

    void putFile( String key, File file );

    void uploadInfoAndResults( Runner metadata, Project project, ISummary summary, File results );

    void uploadTestInfo( Project project );

    Project loadTestInfo();

    Project getTestInfo( String testKey );

    void deleteTests();

    void deleteGhostRunners( Collection<String> activeRunners );
}
