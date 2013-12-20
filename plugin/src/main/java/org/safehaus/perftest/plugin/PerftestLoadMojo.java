package org.safehaus.perftest.plugin;


import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerInfo;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.TestInfo;
import org.safehaus.perftest.client.PerftestClient;
import org.safehaus.perftest.client.PerftestClientModule;
import org.safehaus.perftest.client.ResponseInfo;
import org.safehaus.perftest.client.ssh.SSHCommands;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo(name = "load")
public class PerftestLoadMojo extends PerftestMojo {


    protected PerftestLoadMojo( PerftestMojo mojo ) {
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.accessKey = mojo.accessKey;
        this.secretKey = mojo.secretKey;
        this.bucketName = mojo.bucketName;
        this.destinationParentDir = mojo.destinationParentDir;
        this.managerAppUsername = mojo.managerAppUsername;
        this.managerAppPassword = mojo.managerAppPassword;
        this.testModuleFQCN = mojo.testModuleFQCN;
        this.perftestFormation = mojo.perftestFormation;
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
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    protected PerftestLoadMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {

        getLog().info( "Calling setup goal first to ensure cluster is prepared" );
        PerftestSetupMojo setupMojo = new PerftestSetupMojo( this );
        setupMojo.execute();
        getLog().info( "Cluster is prepared" );

        Injector injector = Guice.createInjector( new PerftestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        Collection<RunnerInfo> runnerCollection = client.getRunners();
        RunnerInfo[] runners = runnerCollection.toArray( new RunnerInfo[ runnerCollection.size() ] ) ;
        RunnerInfo info = null;
        for ( RunnerInfo runner : runners ) {
            info = runner;
            break;
        }

        if ( info == null ) {
            throw new MojoExecutionException( "There is no runner found" );
        }

        AmazonS3 s3 = PerftestUtils.getS3Client( accessKey, secretKey );

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
            ObjectMapper mapper = new ObjectMapper();
            TestInfo currentTestInfo = mapper.readValue( new File( getTestInfoToUploadPath() ), TestInfo.class );
            Set<TestInfo> tests = client.getTests();

            for ( TestInfo test : tests ) {
                if ( currentTestInfo.getGitUuid().equals( test.getGitUuid() ) &&
                        currentTestInfo.getWarMd5().equals( test.getWarMd5() ) ) {
                    testUpToDate = true;
                    break;
                }
            }
        } catch ( Exception e ) {
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

        if ( ! warExists || ! testUpToDate ) {
            getLog().info( "War on store is not up-to-date, calling perftest:deploy goal now..." );
            PerftestDeployMojo deployMojo = new PerftestDeployMojo( this );
            deployMojo.execute();
        }

        getLog().info( "Loading the test on runners..." );

        Result result = client.load( info, getWarOnS3Path(), true );

        if ( !result.getStatus() ) {
            throw new MojoExecutionException( "Could not get the status of runners, quitting..." );
        }

        if ( !result.getState().equals( State.READY ) ) {
            throw new MojoExecutionException(
                    "Something went wrong while trying to load the test, runners are not " + "in ready state" );
        }

        // Restart tomcats on all instances
        getLog().info( "Sending restart tomcat requests to all instances..." );

        Thread[] restarterThreads = new Thread[runners.length];
        SSHRequestThread[] restarters = new SSHRequestThread[runners.length];

        for ( int i = 0; i < runners.length; i++ ) {
            restarters[i] = new SSHRequestThread();
            restarters[i].setSshKeyFile( runnerSSHKeyFile );
            restarters[i].setInstanceURL( runners[i].getHostname() );
            restarterThreads[i] = new Thread( restarters[i] );
            restarterThreads[i].start();
        }

        for ( int i = 0; i < runners.length; i++ ) {
            try {
                restarterThreads[i].join(30000); // Is this enough or too much, should this be an annotated parameter?
            }
            catch ( InterruptedException e ) {
                getLog().warn( "Restart request on " + runners[i].getHostname() + " is interrupted before finish", e );
            }
        }

        ResponseInfo response;
        boolean failedRestart = false;
        for ( int i = 0; i < runners.length; i++ ) {
            response = restarters[i].getResult();
            if ( ! response.isRequestSuccessful() || ! response.isOperationSuccessful() ) {
                for ( String s : response.getMessages() ) {
                    getLog().warn( s );
                }
                for ( String s : response.getErrorMessages() ) {
                    getLog().warn( s );
                }
                failedRestart = true;
            }
        }

        if ( failedRestart ) {
            throw new MojoExecutionException( "There are instances that failed to restart properly, " +
                    "verify the cluster before moving on" );
        }

        getLog().info( "Test war is loaded on each runner instance and in READY state. You can run perftest:start now"
                + " to start your tests" );
    }


    private static class SSHRequestThread implements Runnable {


        private String instanceURL;

        private String sshKeyFile;

        private ResponseInfo result;


        public String getInstanceURL() {
            return instanceURL;
        }


        public void setInstanceURL( final String instanceURL ) {
            this.instanceURL = instanceURL;
        }


        private String getSshKeyFile() {
            return sshKeyFile;
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
