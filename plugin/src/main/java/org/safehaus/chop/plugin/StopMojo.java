package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.api.store.amazon.RunnerInstance;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;
import org.safehaus.chop.client.rest.AsyncRequest;
import org.safehaus.chop.client.rest.StopOp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "stop" )
public class StopMojo extends MainMojo {


    @SuppressWarnings( "UnusedDeclaration" )
    protected StopMojo( MainMojo mojo ) {
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
        this.sleepAfterCreation = mojo.sleepAfterCreation;
        this.setupCreatedInstances = mojo.setupCreatedInstances;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected StopMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup,
                runnerKeyPairName, runnerName, endpoint );
        instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );
        executor = Executors.newFixedThreadPool( instances.size() + 2 );

        if ( instances.size() == 0 ) {
            getLog().warn( "No RUNNING runners were found." );
            return;
        }

        ArrayList<AsyncRequest<RunnerInstance,StopOp>> stops =
                new ArrayList<AsyncRequest<RunnerInstance, StopOp>>( instances.size() );
        for ( Instance instance : instances ) {
            stops.add( new AsyncRequest<RunnerInstance, StopOp>( new RunnerInstance( instance ), new StopOp( instance ) ) );
        }

        try {
            executor.invokeAll( stops );
        } catch ( InterruptedException e ) {
            throw new MojoExecutionException( "Failed on stop invocations." );
        }

//        Injector injector = Guice.createInjector( new ChopClientModule() );
//        ChopClient client = injector.getInstance( ChopClient.class );
//
//        getLog().info( "Stopping runner(s)" );
//
//        Collection<Runner> runners = client.getRunners();
//
//
//
//        if ( runners.size() == 0 ) {
//            throw new MojoExecutionException( "There is no runner found" );
//        }
//
//        Result result;
//        int stoppedCount = 0;
//        for ( Runner runner : runners ) {
//            result = client.stop( runner );
//            if( ! result.getStatus() || result.getState() != State.STOPPED ) {
//                getLog().info( "Could not stop runner at " + result.getEndpoint() );
//            }
//            else {
//                getLog().info( "Stopped runner at " + result.getEndpoint() );
//                stoppedCount++;
//            }
//            getLog().info( "Runner state: " + result.getState() + " , returned message: " + result.getMessage() );
//        }

        getLog().info( "Stopped " + ( stops.size() == 0 ? "no" : stops.size() ) + " runner(s) out of " +
                instances.size() );
    }

}