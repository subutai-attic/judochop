package org.safehaus.perftest.amazon;


import javax.servlet.ServletContext;
import java.io.File;
import java.util.Map;
import java.util.Set;

import org.safehaus.perftest.RunnerInfo;
import org.safehaus.perftest.RunInfo;
import org.safehaus.perftest.TestInfo;


/**
 * The S3 Service is used to register the node so other nodes in the same
 * perftest formation can access it.
 */
public interface AmazonS3Service {

    void start();

    boolean isStarted();

    void stop();

    void triggerScan();

    Set<String> listRunners();

    Set<String> listTests();

    RunnerInfo getRunner( String key );

    Map<String, RunnerInfo> getRunners();

    void setServletContext( ServletContext context );

    RunnerInfo getMyMetadata();

    File download( File tempDir, String perftest ) throws Exception;

    void uploadResults( TestInfo testInfo, RunInfo runInfo, File resultsFile );

    void uploadTestInfo( TestInfo testInfo );
}
