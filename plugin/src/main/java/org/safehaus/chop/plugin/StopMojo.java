package org.safehaus.chop.plugin;


import java.util.Collection;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.State;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

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
    protected StopMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new ChopClientModule() );
        ChopClient client = injector.getInstance( ChopClient.class );

        getLog().info( "Stopping runner(s)" );

        Collection<RunnerFig> runners = client.getRunners();

        if ( runners.size() == 0 ) {
            throw new MojoExecutionException( "There is no runner found" );
        }

        Result result;
        int stoppedCount = 0;
        for ( RunnerFig runner : runners ) {
            result = client.stop( runner );
            if( ! result.getStatus() || result.getState() != State.STOPPED ) {
                getLog().info( "Could not stop runner at " + result.getEndpoint() );
            }
            else {
                getLog().info( "Stopped runner at " + result.getEndpoint() );
                stoppedCount++;
            }
            getLog().info( "Runner state: " + result.getState() + " , returned message: " + result.getMessage() );
        }

        getLog().info( "Stopped " + ( stoppedCount == 0 ? "no" : stoppedCount ) + " runner(s) out of " +
                runners.size() );
    }

}