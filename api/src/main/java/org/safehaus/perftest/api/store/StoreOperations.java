package org.safehaus.perftest.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.TestInfo;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/8/13 Time: 5:23 PM To change this template use File | Settings |
 * File Templates.
 */
public interface StoreOperations {
    String getRunnerKey( String publicHostname );

    void register( RunnerInfo metadata );

    Set<String> getTests();

    Map<String,RunnerInfo> getRunners( String formation );

    File download( File tempDir, String key ) throws IOException;

    <T> T putJsonObject( String key, Object obj );

    void uploadRunInfo( TestInfo testInfo, RunInfo runInfo );

    void putFile( String key, File file );

    void uploadInfoAndResults( RunnerInfo metadata, TestInfo testInfo, RunInfo runInfo, File results );

    void uploadTestInfo( TestInfo testInfo );
}
