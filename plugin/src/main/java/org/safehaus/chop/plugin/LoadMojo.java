package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.safehaus.chop.api.InstallCert;
import org.safehaus.chop.api.ProjectFig;
import org.safehaus.chop.api.ProjectFigBuilder;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.store.amazon.AmazonFig;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.client.PerftestClient;
import org.safehaus.chop.client.PerftestClientModule;
import org.safehaus.chop.client.ResponseInfo;
import org.safehaus.chop.client.ssh.SSHCommands;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "load" )
public class LoadMojo extends MainMojo {

    protected LoadMojo( MainMojo mojo ) {
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
    protected LoadMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {

        /* ------------------------------------------------------------------------------
         * This ensures the proper instance types and counts are up and ready.
         * ------------------------------------------------------------------------------
         */

        getLog().info( "Calling setup goal first to ensure cluster is prepared" );
        SetupMojo setupMojo = new SetupMojo( this );
        setupMojo.execute();
        getLog().info( "Cluster is prepared" );

        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        /* ------------------------------------------------------------------------------
         * Instances may be running but may not be registered their runner information in
         * S3, so we're going to need to access instance information directly with EC2
         * Manager rather than from S3.
         * ------------------------------------------------------------------------------
         */

        EC2Manager ec2Manager =
                new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup, runnerKeyPairName, runnerName );

        try {
            InstanceType type = InstanceType.valueOf( instanceType );
            ec2Manager.setDefaultType( type );
        }
        catch ( IllegalArgumentException e ) {
            getLog().warn( "The instanceType provided " + instanceType + " was not a valid InstanceType String" );
        }
        catch ( NullPointerException e ) {
            getLog().info( "Instance type value was not provided. Using EC2Manager default." );
        }

        ec2Manager.setAvailabilityZone( availabilityZone );
        ec2Manager.setDefaultTimeout( setupTimeout );
        Collection<Instance> instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );

        /*
         * Let's check and see if the project file exists and if it does not that means we
         * need to build the war and thus invoke the war mojo.
         */


        File projectFile = new File( getProjectFileToUploadPath() );
        if ( ! projectFile.exists() ) {
            getLog().warn( "It seems as though the project properties file " + projectFile
                    + " does not exist. Creating it and the war now." );
            WarMojo warMojo = new WarMojo( this );
            warMojo.execute();

            if ( projectFile.exists() ) {
                getLog().info( "War is generated and project file exists." );
            }
            else {
                throw new MojoExecutionException( "Failed to generate the project.properties." );
            }
        }

        // Load the project configuration from the file system
        ProjectFig projectFig;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( projectFile ) );
            ProjectFigBuilder builder = new ProjectFigBuilder( props );
            projectFig = builder.getProject();
        }
        catch ( Exception e ) {
            getLog().warn( "Error accessing project information from local filesystem: " + getProjectFileToUploadPath(),
                    e );
            throw new MojoExecutionException(
                    "Cannot access local file system based project information: " + getProjectFileToUploadPath(), e );
        }

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
            Set<ProjectFig> tests = client.getProjectConfigs();
            for ( ProjectFig test : tests ) {
                if ( projectFig.getVcsRepoUrl() != null &&
                        projectFig.getWarMd5() != null &&
                        projectFig.getVcsVersion().equals( test.getVcsVersion() ) &&
                        projectFig.getWarMd5().equals( test.getWarMd5() ) ) {
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


        // These are the values we're going to send to load to setup S3 properly
        Map<String, String> overrides = new HashMap<String, String>( 3 );
        overrides.put( AmazonFig.AWS_SECRET_KEY, secretKey );
        overrides.put( AmazonFig.AWS_BUCKET_KEY, bucketName );
        overrides.put( AmazonFig.AWSKEY_KEY, accessKey );
        overrides.put( ProjectFig.MANAGER_USERNAME_KEY, managerAppUsername );
        overrides.put( ProjectFig.MANAGER_PASSWORD_KEY, managerAppPassword );

        // @todo need to figure out why the default value does not hold for the manager endpoint
        // overrides.put( ProjectFig.MANAGER_ENDPOINT_KEY, projectFig.getManagerEndpoint() );

        // This array holds the instances that have loaded a new runner, so absolutely requires restart on container
        Collection<Instance> instancesToRestart = new ArrayList<Instance>( instances.size() );

        for ( Instance instance : instances ) {
            getLog().info( "Checking out instance " + instance.getPublicDnsName() );

            RunnerFig runnerFig = injector.getInstance( RunnerFig.class );
            runnerFig.bypass( RunnerFig.HOSTNAME_KEY, instance.getPublicDnsName() );
            runnerFig.bypass( RunnerFig.SERVER_PORT_KEY, RunnerFig.DEFAULT_SERVER_PORT );
            runnerFig.bypass( RunnerFig.IPV4_KEY, instance.getPublicIpAddress() );
            runnerFig.bypass( RunnerFig.URL_KEY,
                    "https://" + instance.getPublicDnsName() + ":" + RunnerFig.DEFAULT_SERVER_PORT + "/" );

            try {
                InstallCert.installCert( runnerFig.getHostname(), runnerFig.getServerPort(), null );
            }
            catch ( Exception e ) {
                getLog().error( "Failed to install the server cert.", e );
            }

            // First let's check the instance's status and what it is running
            Result result = client.status( runnerFig );
            getLog().info( "Instance " + instance.getPublicDnsName() + " is in state " + result.getState() );
            getLog().info( "Instance " + instance.getPublicDnsName() + " is has the following project setup " + result
                    .getProject() );

            if ( !result.getState().accepts( Signal.LOAD ) ) {
                getLog().warn( "Instance " + instance.getPublicDnsName() + " not ready to load in state " + result
                        .getState() );
            }

            if ( result.getProject() != null && result.getProject().getWarMd5().equals( projectFig.getWarMd5() ) ) {
                getLog().info(
                        "Skipping instance " + instance.getPublicDnsName() + " it is loaded with the same project." );
                continue;
            }

            String gitConfigDirectory = Utils.getGitConfigFolder( getProjectBaseDirectory() );
            String commitId = Utils.getLastCommitUuid( gitConfigDirectory );
            String uuid = commitId.substring( 0, CHARS_OF_UUID/2 ) +
                    commitId.substring( commitId.length() - CHARS_OF_UUID/2 );
            String loadKey = CONFIGS_PATH + '/' + uuid + '/' + RUNNER_WAR;
            result = client.load( runnerFig, loadKey, overrides );

            instancesToRestart.add( instance );

            if ( !result.getStatus() ) {
                throw new MojoExecutionException( "Load problem on " + instance.getPublicDnsName() + " in state " +
                        result.getState() + ": " + result.getMessage() );
            }
        }

        /**
         * If coldRestartTomcat is true, we are restarting tomcats for all instances
         * If not, only the newly loaded ones are restarted
         */
        if( coldRestartTomcat ) {
            instancesToRestart.clear();
            instancesToRestart = instances;
            getLog().info( "Sending restart tomcat requests to all instances..." );
        }
        else {
            getLog().info( "Sending restart tomcat requests to loaded instances..." );
        }

        List<SSHRequestThread> restarters = new ArrayList<SSHRequestThread>( instancesToRestart.size() );

        for ( Instance instance : instancesToRestart ) {
            SSHRequestThread restarter = new SSHRequestThread();
            restarter.setSshKeyFile( runnerSSHKeyFile );
            restarter.setInstanceURL( instance.getPublicDnsName() );
            restarter.setInstance( instance );
            restarters.add( restarter );
            restarter.start();
        }

        for ( SSHRequestThread restarter : restarters ) {
            try {
                restarter.join( 40000 ); // Is this enough or too much, should this be an annotated parameter?
            }
            catch ( InterruptedException e ) {
                getLog().warn( "Restart request on " + restarter.getInstance().getPublicDnsName()
                        + " is interrupted before finish", e );
            }
        }

        ResponseInfo response;
        boolean failedRestart = false;
        for ( SSHRequestThread restarter : restarters ) {
            response = restarter.getResult();
            if ( !response.isRequestSuccessful() || !response.isOperationSuccessful() ) {
                for ( String s : response.getMessages() ) {
                    getLog().info( s );
                }
                for ( String s : response.getErrorMessages() ) {
                    getLog().info( s );
                }
                failedRestart = true;
            }
        }

        if ( failedRestart ) {
            throw new MojoExecutionException(
                    "There are instances that failed to restart properly, " + "verify the cluster before moving on" );
        }

        getLog().info( "Test war is loaded on each runner instance and in READY state. You can run chop:start now"
                + " to start your tests" );
    }


    private static class SSHRequestThread implements Runnable {


        private String instanceURL;

        private String sshKeyFile;

        private ResponseInfo result;

        private Instance instance;

        private Thread thread;


        public void start() {
            thread = new Thread( this );
            thread.start();
        }


        public void join( long timeout ) throws InterruptedException {
            thread.join( timeout );
        }


        public Instance getInstance() {
            return instance;
        }


        public void setInstance( Instance instance ) {
            this.instance = instance;
        }


        public void setInstanceURL( final String instanceURL ) {
            this.instanceURL = instanceURL;
        }


        private void setSshKeyFile( final String sshKeyFile ) {
            this.sshKeyFile = sshKeyFile;
        }


        public ResponseInfo getResult() {
            return result;
        }


        @Override
        public void run() {
            result = SSHCommands.restartTomcatOnInstance( sshKeyFile, instanceURL );
        }
    }
}
