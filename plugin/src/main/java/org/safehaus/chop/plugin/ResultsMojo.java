package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.provider.Store;
import org.safehaus.chop.api.store.amazon.AmazonStoreModule;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.api.store.amazon.RunnerInstance;
import org.safehaus.chop.client.rest.AsyncRequest;
import org.safehaus.chop.client.rest.StatusOp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
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
        this.runnerCount = mojo.runnerCount;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.availabilityZone = mojo.availabilityZone;
        this.resetIfStopped = mojo.resetIfStopped;
        this.coldRestartTomcat = mojo.coldRestartTomcat;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.setupCreatedInstances = mojo.setupCreatedInstances;
        this.sleepAfterCreation = mojo.sleepAfterCreation;
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
                return name.endsWith( SUMMARY_SUFFIX )
                        ||
                        name.endsWith( RESULTS_SUFFIX )
                        ||
                        name.endsWith( PROJECT_FILE );
            }
        };
        FilenameFilter summaryOnly = new FilenameFilter() {
            @Override
            public boolean accept( final File dir, final String name ) {
                return name.endsWith( SUMMARY_SUFFIX ) || name.endsWith( PROJECT_FILE );
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
                    sb.append( ChopUtils.getTestBase( project ) )
                      .append( String.valueOf( nextRunNumber - 1 ) ).append( '/' );
                    try {
                        store.download( resultsDirectory, sb.toString(), summaryAndResults );
                        sb.setLength( 0 );
                        sb.append( ChopUtils.getTestBase( project ) ).append( PROJECT_FILE );
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

        if ( blockUntilComplete ) {
            while ( testInProgress() ) {
                getLog().info( "Tests in progress, blocking until they complete." );

                try {
                    Thread.sleep( 120000 );
                }
                catch ( InterruptedException e ) {
                    getLog().warn( "Thread interrupted." );
                }
            }

            getLog().info( "It seems the tests have completed, resuming results download." );
        }
        else {
            if ( testInProgress() ) {
                throw new MojoExecutionException( "Tests in progress, results download aborted." );
            }
        }
    }


    private boolean testInProgress() throws MojoExecutionException {
        EC2Manager ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup,
                runnerKeyPairName, runnerName, endpoint );
        Collection<Instance> instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );

        ArrayList<AsyncRequest<RunnerInstance,StatusOp>> statuses =
                new ArrayList<AsyncRequest<RunnerInstance, StatusOp>>( instances.size() );

        for ( Instance instance : instances ) {
            statuses.add( new AsyncRequest<RunnerInstance, StatusOp>( new RunnerInstance( instance ), new StatusOp( instance ) ) );
        }

        ExecutorService service = Executors.newFixedThreadPool( instances.size() );
        try {
            service.invokeAll( statuses );
        }
        catch ( InterruptedException e ) {
            throw new MojoExecutionException( "Failed on status invocations.", e );
        }

        for ( AsyncRequest<RunnerInstance,StatusOp> request : statuses ) {
            if ( request.getRestOperation().getResult().getState() == State.RUNNING ) {
                return true;
            }
        }

        return false;
    }
}

