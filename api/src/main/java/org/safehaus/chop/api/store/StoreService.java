package org.safehaus.chop.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.RunInfo;
import org.safehaus.chop.api.RunnerInfo;
import org.safehaus.chop.api.TestInfo;


/** The S3 Service is used to register the node so other nodes in the same perftest formation can access it. */
public interface StoreService {

    void start();

    boolean isStarted();

    void stop();

    void triggerScan();

    Set<String> listRunners();

    Set<TestInfo> listTests() throws IOException;

    RunnerInfo getRunner( String key );

    Map<String, RunnerInfo> getRunners();

    RunnerInfo getMyMetadata();

    File download( File tempDir, String perftest ) throws Exception;

    void uploadResults( TestInfo testInfo, RunInfo runInfo, File resultsFile );

    void uploadTestInfo( TestInfo testInfo );

    TestInfo loadTestInfo();
}
