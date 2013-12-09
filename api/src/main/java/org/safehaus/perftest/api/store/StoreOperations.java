package org.safehaus.perftest.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.TestInfo;


/** Operations performed against a perftest store. */
public interface StoreOperations {
    String getRunnerKey( String publicHostname );

    void register( RunnerInfo metadata );

    Set<TestInfo> getTests() throws IOException;

    Map<String, RunnerInfo> getRunners( RunnerInfo runner );

    Map<String, RunnerInfo> getRunners();

    File download( File tempDir, String key ) throws IOException;

    <T> T putJsonObject( String key, Object obj );

    void uploadRunInfo( TestInfo testInfo, RunInfo runInfo );

    void putFile( String key, File file );

    void uploadInfoAndResults( RunnerInfo metadata, TestInfo testInfo, RunInfo runInfo, File results );

    void uploadTestInfo( TestInfo testInfo );

    TestInfo loadTestInfo();

    TestInfo getTestInfo( String testKey );

    void deleteTests();
}
