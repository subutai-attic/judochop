package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Store;
import org.safehaus.chop.api.store.amazon.AmazonStoreModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 * This mojo implements the results goal, which downloads all or a partial
 * set of results from the store.
 */
@Mojo( name = "results" )
public class ResultsMojo extends MainMojo {

    enum DumpType {
        FULL, SUMMARY, TEST, RUN
    }

    private DumpType type;


    @SuppressWarnings( "UnusedDeclaration" )
    protected ResultsMojo( MainMojo mojo ) {
        this.resultsDirectory = mojo.resultsDirectory;
        this.dumpType = mojo.dumpType;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.accessKey = mojo.accessKey;
        this.secretKey = mojo.secretKey;
        this.bucketName = mojo.bucketName;
        this.destinationParentDir = mojo.destinationParentDir;
        this.managerAppUsername = mojo.managerAppUsername;
        this.managerAppPassword = mojo.managerAppPassword;
        this.testPackageBase = mojo.testPackageBase;
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.amiID = mojo.amiID;
        this.awsSecurityGroup = mojo.awsSecurityGroup;
        this.runnerKeyPairName = mojo.runnerKeyPairName;
        this.runnerName = mojo.runnerName;
        this.instanceType = mojo.instanceType;
        this.setupTimeout = mojo.setupTimeout;
        this.minimumRunners = mojo.minimumRunners;
        this.maximumRunners = mojo.maximumRunners;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.availabilityZone = mojo.availabilityZone;
        this.resetIfStopped = mojo.resetIfStopped;
        this.coldRestartTomcat = mojo.coldRestartTomcat;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected ResultsMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        preconditions();

        Injector injector = Guice.createInjector( new AmazonStoreModule() );
        Store store = injector.getInstance( Store.class );
        Project project = loadProjectConfiguration();
        FilenameFilter summaryAndResults = new FilenameFilter() {
            @Override
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( SUMMARY_SUFFIX ) || name.endsWith( RESULTS_SUFFIX );
            }
        };
        FilenameFilter summaryOnly = new FilenameFilter() {
            @Override
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( SUMMARY_SUFFIX );
            }
        };

        switch ( type ) {
            case FULL:
                try {
                    store.download( resultsDirectory, TESTS_PATH, summaryAndResults );
                }
                catch ( Exception e ) {
                    throw new MojoExecutionException( "Failed to download all summaries and results.", e );
                }
                break;
            case SUMMARY:
                try {
                    store.download( resultsDirectory, TESTS_PATH, summaryOnly );
                }
                catch ( Exception e ) {
                    throw new MojoExecutionException( "Failed to download all summaries only.", e );
                }
                break;
            case TEST:
                String test = ChopUtils.getTestBase( project );
                try {
                    store.download( resultsDirectory, test, summaryAndResults );
                }
                catch ( Exception e ) {
                    throw new MojoExecutionException( "Failed to download summaries and results for test: " + test, e );
                }
                break;
            case RUN:
                int nextRunNumber = store.getNextRunNumber( project );
                if ( nextRunNumber < 2 ) {
                    getLog().warn( "No runs exist yet for the specified project!" );
                }
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append( TESTS_PATH ).append( '/' )
                      .append( String.valueOf( nextRunNumber - 1 ) ).append( '/' );
                    try {
                        store.download( resultsDirectory, sb.toString(), summaryAndResults );
                    }
                    catch ( Exception e ) {
                        throw new MojoExecutionException( "Failed to download summaries and results for test run: "
                                + sb.toString() , e );
                    }
                }
                break;
        }
    }


    private void preconditions() throws MojoExecutionException {
        Preconditions.checkNotNull( resultsDirectory );
        Preconditions.checkNotNull( dumpType );

        if ( ! resultsDirectory.exists() ) {
            if ( ! resultsDirectory.mkdirs() ) {
                throw new MojoExecutionException( "Cannot create resultsDirectory "
                        + resultsDirectory.getAbsolutePath() );
            }
        }
        else if ( ! resultsDirectory.isDirectory() ) {
            throw new MojoExecutionException( "Path to resultsDirectory " + resultsDirectory.getAbsolutePath()
                    + " is not a directory." );
        }
        else if ( ! resultsDirectory.canWrite() ) {
            throw new MojoExecutionException( "No permission to write to resultsDirectory "
                    + resultsDirectory.getAbsolutePath() );
        }

        try {
            type = DumpType.valueOf( dumpType );
        }
        catch ( IllegalArgumentException iae ) {
            throw new MojoExecutionException( "Provided value '" + dumpType
                    + "' for dumpType does not match a valid dumpType enum: "
                    + Arrays.toString( DumpType.values() ), iae );
        }
    }
}

