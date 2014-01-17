package org.safehaus.chop.plugin;


import java.util.Collection;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.State;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "reset" )
public class ResetMojo extends MainMojo {


    protected ResetMojo( MainMojo mojo ) {
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


    protected ResetMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new ChopClientModule() );
        ChopClient client = injector.getInstance( ChopClient.class );

        getLog().info( "Resetting stopped runner(s)" );

        Collection<Runner> runners = client.getRunners();

        if ( runners.size() == 0 ) {
            throw new MojoExecutionException( "There is no runner found" );
        }

        Result result;
        int resetCount = 0;
        for ( Runner runner : runners ) {
            result = client.reset( runner );
            if( ! result.getStatus() || result.getState() != State.READY ) {
                getLog().info( "Could not reset runner at " + result.getEndpoint() );
            }
            else {
                getLog().info( "Reset runner at " + result.getEndpoint() );
                resetCount++;
            }
            getLog().info( "Runner state: " + result.getState() + " , returned message: " + result.getMessage() );
        }

        getLog().info( "Reset " + ( resetCount == 0 ? "no" : resetCount ) + " runner(s) out of " + runners.size() );
    }
}
