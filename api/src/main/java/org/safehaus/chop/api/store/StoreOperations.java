package org.safehaus.chop.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.Runner;


/** Operations performed against a perftest store. */
public interface StoreOperations {
    String getRunnerKey( String publicHostname );

    void register( Runner metadata );

    Set<ProjectFig> getProjects() throws IOException;

    Map<String, Runner> getRunners( Runner runner );

    Map<String, Runner> getRunners();

    File download( File tempDir, String key ) throws IOException;

    <T> T putJsonObject( String key, Object obj );

    void store( Runner metadata, ProjectFig project, ISummary summary );

    void putFile( String key, File file );

    void store( Runner metadata, ProjectFig project, ISummary summary, File results );

    void store( ProjectFig project );

    ProjectFig getProject();

    ProjectFig getProject( String testKey );

    void deleteProjects();

    void deleteGhostRunners( Collection<String> activeRunners );
}
