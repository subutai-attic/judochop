package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.store.amazon.AmazonFig;
import org.safehaus.chop.api.store.amazon.InstanceValues;
import org.safehaus.chop.api.store.amazon.RunnerInstance;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;
import org.safehaus.chop.client.ssh.AsyncSsh;
import org.safehaus.chop.client.rest.AsyncRequest;
import org.safehaus.chop.client.rest.LoadOp;
import org.safehaus.chop.client.rest.StatusOp;
import org.safehaus.chop.client.ssh.TomcatRestart;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "load", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class LoadMojo extends MainMojo {

    protected LoadMojo( MainMojo mojo ) {
        this.accessKey = mojo.accessKey;
        this.amiID = mojo.amiID;
        this.availabilityZone = mojo.availabilityZone;
        this.awsSecurityGroup = mojo.awsSecurityGroup;
        this.blockUntilComplete = mojo.blockUntilComplete;
        this.bucketName = mojo.bucketName;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.coldRestartTomcat = mojo.coldRestartTomcat;
        this.destinationParentDir = mojo.destinationParentDir;
        this.dumpType = mojo.dumpType;
        this.endpoint = mojo.endpoint;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.instanceType = mojo.instanceType;
        this.localRepository = mojo.localRepository;
        this.managerAppPassword = mojo.managerAppPassword;
        this.managerAppUsername = mojo.managerAppUsername;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.resetIfStopped = mojo.resetIfStopped;
        this.resultsDirectory = mojo.resultsDirectory;
        this.runnerCount = mojo.runnerCount;
        this.runnerKeyPairName = mojo.runnerKeyPairName;
        this.runnerName = mojo.runnerName;
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.secretKey = mojo.secretKey;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.setupCreatedInstances = mojo.setupCreatedInstances;
        this.setupTimeout = mojo.setupTimeout;
        this.sleepAfterCreation = mojo.sleepAfterCreation;
        this.store = mojo.store;
        this.testPackageBase = mojo.testPackageBase;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected LoadMojo() {

    }


    protected void setSshValues() {
        Preconditions.checkNotNull( runnerSSHKeyFile );
        Preconditions.checkNotNull( managerAppPassword );

        list = new InstanceValues( "ls", runnerSSHKeyFile );

        String stop = "sudo service tomcat7 stop; ";
        stopTomcat = new InstanceValues( stop, runnerSSHKeyFile );

        String start = "sudo service tomcat7 start; ";
        startTomcat = new InstanceValues( start, runnerSSHKeyFile );

        String status = "sudo service tomcat7 status; ";
        statusTomcat = new InstanceValues( status, runnerSSHKeyFile );

        String password = "sudo sed -i 's/WtPgEXEwioa0LXUtqGwtkC4YUSgl2w4BF9VXsBviT/" + managerAppPassword
                + "/g' /etc/tomcat7/tomcat-users.xml; ";
        passwordTomcat = new InstanceValues( password, runnerSSHKeyFile );

        String combined = stop + password + start + status;
        stopPasswordStartStatusTomcat = new InstanceValues( combined, runnerSSHKeyFile );
    }


    @Override
    public void execute() throws MojoExecutionException {
        setSshValues();

        /* ------------------------------------------------------------------------------
         * This ensures the proper instance types and counts are up and ready.
         * ------------------------------------------------------------------------------
         */

        getLog().info( "" );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "Calling setup goal first to ensure cluster is prepared" );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "" );

        SetupMojo setupMojo = new SetupMojo( this );
        setupMojo.execute();
        getLog().info( "Cluster is prepared" );

        getLog().info( "" );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "RUNNER CLUSTER IS PREPARED" );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "" );

        // @todo see if it works without this - don't think we need it anymore
        //
//        /*
//         * Sometimes load gets a net connection exception due to the runners not being up
//         * after a setup has created instances. In this case we want to wait a little bit
//         * until those runners are up.
//         */
//
//        if ( setupMojo.setupCreatedInstances ) {
//            getLog().info( "Seems setup created instances, so we'll wait " + sleepAfterCreation
//                    + " milliseconds to make sure they come up." );
//
//            try {
//                Thread.sleep( sleepAfterCreation );
//            }
//            catch ( InterruptedException e ) {
//                getLog().warn( "Awe snap! Could not wait the full " + sleepAfterCreation
//                        + " milliseconds after instance creation." );
//            }
//        }

        Injector injector = Guice.createInjector( new ChopClientModule() );
        ChopClient client = injector.getInstance( ChopClient.class );

        /*
         * Let's check and see if the project file exists and if it does not that means we
         * need to build the war and thus invoke the war mojo.
         */

        // Load the project configuration from the file system
        Project project = loadProjectConfiguration();

        /* ------------------------------------------------------------------------------
         * Before dealing with instances let's check and make sure the latest runner
         * war software is deployed to S3 now that we have the project information loaded
         * ------------------------------------------------------------------------------
         */

        AmazonS3 s3 = Utils.getS3Client( accessKey, secretKey );

        Bucket bucket = null;
        for ( Bucket b : s3.listBuckets() ) {
            if ( b.getName().equals( bucketName ) ) {
                bucket = b;
                break;
            }
        }

        if ( bucket == null ) {
            throw new MojoExecutionException( bucketName + " bucket is not found with given credentials" );
        }

        // Check if the latest war is deployed on Store
        boolean testUpToDate = false;
        try {
            Set<Project> tests = client.getProjectConfigs();
            for ( Project test : tests ) {
                if ( project.getVcsRepoUrl() != null &&
                        project.getWarMd5() != null &&
                        project.getVcsVersion().equals( test.getVcsVersion() ) &&
                        project.getWarMd5().equals( test.getWarMd5() ) ) {
                    testUpToDate = true;
                    break;
                }
            }
        }
        catch ( Exception e ) {
            getLog().warn( "Error while getting test information from store", e );
        }

        String warOnS3Path = getWarOnS3Path();
        boolean warExists = false;

        for ( S3ObjectSummary file : s3.listObjects( bucketName ).getObjectSummaries() ) {
            if ( warOnS3Path.equals( file.getKey() ) ) {
                warExists = true;
                break;
            }
        }

        if ( !warExists || !testUpToDate ) {
            getLog().info( "War on store is not up-to-date, calling chop:deploy goal now..." );
            DeployMojo deployMojo = new DeployMojo( this );
            deployMojo.execute();
        }


        /* ------------------------------------------------------------------------------
         * Here we setup all the asynchronous status requests to be issued in parallel
         * to runner instances and invoke them all at the same time.
         * ------------------------------------------------------------------------------
         */

        // This array holds the instances that have loaded a new runner, so absolutely requires restart on container
        Collection<Instance> instancesToRestart = new ArrayList<Instance>( instances.size() );
        ArrayList<AsyncRequest<RunnerInstance,StatusOp>> statuses =
                new ArrayList<AsyncRequest<RunnerInstance, StatusOp>>( instances.size() );

        for ( Instance instance : instances ) {
            statuses.add( new AsyncRequest<RunnerInstance, StatusOp>( new RunnerInstance( instance ), new StatusOp( instance ) ) );
        }

        try {
            executor.invokeAll( statuses );
        }
        catch ( InterruptedException e ) {
            throw new MojoExecutionException( "Failed on status invocations.", e );
        }


        /* ------------------------------------------------------------------------------
         * Based on the status results we will issue a load in parallel. However before
         * we can do that we need to calculate some values that we need for the load
         * request parameters like the loadKey and other parameters.
         * ------------------------------------------------------------------------------
         */

        String gitConfigDirectory = Utils.getGitConfigFolder( getProjectBaseDirectory() );
        String commitId = Utils.getLastCommitUuid( gitConfigDirectory );
        String uuid = commitId.substring( 0, CHARS_OF_UUID/2 ) +
                commitId.substring( commitId.length() - CHARS_OF_UUID/2 );
        String loadKey = TESTS_PATH + '/' + uuid + '/' + RUNNER_WAR;
        // These are the values we're going to send to load to setup S3 properly
        Map<String, String> overrides = new HashMap<String, String>( 3 );
        overrides.put( AmazonFig.AWS_SECRET_KEY, secretKey );
        overrides.put( AmazonFig.AWS_BUCKET_KEY, bucketName );
        overrides.put( AmazonFig.AWSKEY_KEY, accessKey );
        overrides.put( Project.MANAGER_USERNAME_KEY, managerAppUsername );
        overrides.put( Project.MANAGER_PASSWORD_KEY, managerAppPassword );
        overrides.put( Constants.PARAM_PROJECT, loadKey );

        // @todo need to figure out why the default value does not hold for the manager endpoint
        // overrides.put( Project.MANAGER_ENDPOINT_KEY, project.getManagerEndpoint() );


        /* ------------------------------------------------------------------------------
         * Here we iterate through the asynchronous requests for the status operation
         * and depending on the results leave the runner as is or prepare to issue a
         * load operation against it. Runner instances to be loaded are also added to
         * the list of instances to be restarted. If any of the status requests failed
         * with an exception the mojo fails.
         * ------------------------------------------------------------------------------
         */

        ArrayList<AsyncRequest<RunnerInstance,LoadOp>> loads = new ArrayList<AsyncRequest<RunnerInstance, LoadOp>>();
        for ( AsyncRequest<RunnerInstance,StatusOp> status : statuses ) {
            Result result = status.getRestOperation().getResult();

            if ( status.failed() || ! result.getStatus() ) {
                throw new MojoExecutionException( "Status on Runner " + status.getAssociate().getHostname() +
                        " failed:", status.getException() );
            }

            Project current = result.getProject();
            if ( current != null && current.getWarMd5().equals( project.getWarMd5() ) ) {
                getLog().info( "Runner " + status.getAssociate().getHostname() +
                        " has the same test already loaded: MD5 signatures match." );
                continue;
            }

            if ( result.getState().accepts( Signal.LOAD ) ) {
                getLog().info( "Runner " + status.getAssociate().getHostname() +
                        " needs to be updated with the latest test: MD5 signatures do NOT match." );
                loads.add( new AsyncRequest<RunnerInstance, LoadOp>( status.getAssociate(),
                        new LoadOp( status.getAssociate(), overrides ) ) );
                instancesToRestart.add( status.getAssociate().getInstance() );
            }
            else {
                throw new MojoExecutionException( "Runner " + status.getAssociate().getHostname() +
                    " must be updated, however it cannot be loaded in state " + result.getState() );
            }
        }


        /* ------------------------------------------------------------------------------
         * Here we run all load operations in parallel if there exist runner instances
         * that are not up to date with the latest runner war file. If all are up to date
         * no parallel load is issued. If some need an update the load is issued.
         * ------------------------------------------------------------------------------
         */

        getLog().info( "Updating Runners:" );
        for ( Instance instance : instancesToRestart ) {
            getLog().info( "\t\t" + instance.getPublicDnsName() + " (" + instance.getInstanceId() + ")" );
        }

        if ( loads.size() > 0 ) {
            try {
                executor.invokeAll( loads );
            }
            catch ( InterruptedException e ) {
                throw new MojoExecutionException( "Failed on status invocations.", e );
            }
        }
        else {
            getLog().info( "No load requests issued." );
        }

        for ( AsyncRequest<RunnerInstance, LoadOp> load : loads ) {
            Result result = load.getRestOperation().getResult();

            if ( load.failed() || ! result.getStatus() ) {
                throw new MojoExecutionException( "Load on Runner " + load.getAssociate().getHostname() +
                        " failed:", load.getException() );
            }
        }


        /**
         * There's a 2s pause in the Runner's load REST operation returns, but before the
         * reload of the application is issued for safety purposes. We must make sure we
         * give the Tomcat Admin Application to reload the file.
         */
        getLog().info( "" );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "Sending restart command to newly loaded runner Tomcat instances ..." );
        getLog().info( "------------------------------------------------------------------------" );
        getLog().info( "" );

        boolean allUp;

        String stop = "sudo service tomcat7 stop; ";
        String start = "sudo service tomcat7 start; ";
        String status = "sudo service tomcat7 status; ";
        String password = "sudo sed -i 's/WtPgEXEwioa0LXUtqGwtkC4YUSgl2w4BF9VXsBviT/" + managerAppPassword
                + "/g' /etc/tomcat7/tomcat-users.xml; ";
        String combined = stop + password + start + status;
        stopPasswordStartStatusTomcat = new InstanceValues( combined, runnerSSHKeyFile );

        Collection<TomcatRestart<Instance>> failedTomcat = TomcatRestart.getCommands( managerAppPassword,
                instancesToRestart, stopPasswordStartStatusTomcat );
        do {
            getLog().info( "Sleeping for 5 seconds" );
            try {
                Thread.sleep( 5000 );
            }
            catch ( InterruptedException e ) {
                getLog().warn( "Got interrupted.", e );
            }

            getLog().info( "Woke up from sleep ready to parallel execute Tomcat restart commands." );

            try {
                getLog().info( "About to execute " + failedTomcat.size() + " commands." );

                executor.invokeAll( failedTomcat );

                failedTomcat = TomcatRestart.extractTomcatFailures( failedTomcat );
                getLog().info( failedTomcat.size() + " of those commands failed." );

                if ( failedTomcat.size() > 0 ) {
                    getLog().info( "Failed on ssh commands: " );
                    for ( AsyncSsh<Instance> command : failedTomcat ) {
                        getLog().info( "\t\t" + command );
                    }
                }

                allUp = failedTomcat.isEmpty();
            }
            catch ( Exception e ) {
                getLog().error( "Got the following error during command execution.", e );
                allUp = false;
            }
        } while ( ! allUp );


//        List<SSHRequestThread> restarters = new ArrayList<SSHRequestThread>( instancesToRestart.size() );
//
//        for ( Instance instance : instancesToRestart ) {
//            SSHRequestThread restarter = new SSHRequestThread();
//            restarter.setSshKeyFile( runnerSSHKeyFile );
//            restarter.setInstanceURL( instance.getPublicDnsName() );
//            restarter.setTomcatAdminPassword( managerAppPassword );
//            restarter.setInstance( instance );
//            restarters.add( restarter );
//            restarter.start();
//        }
//
//        for ( SSHRequestThread restarter : restarters ) {
//            try {
//                restarter.join( 40000 ); // Is this enough or too much, should this be an annotated parameter?
//            }
//            catch ( InterruptedException e ) {
//                getLog().warn( "Restart request on " + restarter.getInstance().getPublicDnsName()
//                        + " is interrupted before finish", e );
//            }
//        }
//
//        ResponseInfo response;
//        boolean failedRestart = false;
//        for ( SSHRequestThread restarter : restarters ) {
//            response = restarter.getResult();
//            if ( !response.isRequestSuccessful() || !response.isOperationSuccessful() ) {
//                for ( String s : response.getMessages() ) {
//                    getLog().info( s );
//                }
//                for ( String s : response.getErrorMessages() ) {
//                    getLog().info( s );
//                }
//                failedRestart = true;
//            }
//        }
//
//        if ( failedRestart ) {
//            throw new MojoExecutionException(
//                    "There are instances that failed to restart properly, verify the cluster before moving on" );
//        }

        getLog().info( "Test war is loaded on each runner instance and in READY state." );
    }
}
