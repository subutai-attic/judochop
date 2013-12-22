package org.safehaus.chop.api.store;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;


/** The S3 Service is used to register the node so other nodes in the same perftest formation can access it. */
public interface StoreService {

    void start();

    boolean isStarted();

    void stop();

    void triggerScan();

    Set<String> listRunners();

    Set<Project> listTests() throws IOException;

    Runner getRunner( String key );

    Map<String, Runner> getRunners();

    Runner getMyMetadata();

    File download( File tempDir, String perftest ) throws Exception;

    void uploadResults( Project project, ISummary summary, File resultsFile );

    void uploadTestInfo( Project project );

    Project loadTestInfo();
}
