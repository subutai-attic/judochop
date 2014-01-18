package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;
import org.safehaus.chop.client.ResponseInfo;
import org.safehaus.chop.client.ssh.SSHCommands;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo ( name = "start" )
public class StartMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {
        Injector injector = Guice.createInjector( new ChopClientModule() );
        ChopClient client = injector.getInstance( ChopClient.class );

        // Always call load goal first since it is already making all the controls and next in the plugin chain scheme
        LoadMojo loadMojo = new LoadMojo( this );
        loadMojo.execute();

        /**
         * After having possible restarts on tomcats, runners may need some time to register themselves to the store
         * So we are waiting until all instances have their runners registered on the store or a timeout occurs
         */
        getLog().info( "Checking and waiting maximum " + setupTimeout +
                " msecs. until all runners register themselves to the store" );
        EC2Manager ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup,
                runnerKeyPairName, runnerName, endpoint );
        Collection<Instance> instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );
        long startTime = System.currentTimeMillis();
        boolean runnersRegistered = false;
        Collection<Runner> runners = null;
        while ( System.currentTimeMillis() - startTime < setupTimeout && !runnersRegistered ) {
            runners = client.getRunners();
            // We are not checking here if runners correspond to instances 1-to-1 but this is theoretically safe
            // since we are checking cluster and removing ghostRunner records from store before coming here
            if( instances.size() == runners.size() ) {
                runnersRegistered = true;
            }
        }

        if( !runnersRegistered ) {
            throw new MojoExecutionException( "Not all runners could register themselves to the store before time" );
        }

        /**
         * Verify the status of runners
         *   If at least one runner is in stopped state and resetIfStopped parameter of plugin is set,
         *          execute reset goal and try to verify again;
         * - If verification result in all runners with running state, set callLoadGoal to false;
         * - If callLoadGoal is true, execute load goal in chain;
         */
        Result verifyResult = client.verify();
        if( verifyResult.getStatus() && verifyResult.getState().equals( State.STOPPED ) ) {
            if( resetIfStopped ) {
                getLog().info( "There is at least one runner in STOPPED state, calling reset goal..." );
                ResetMojo resetMojo = new ResetMojo( this );
                resetMojo.execute();
                verifyResult = client.verify();
            }
            else {
                throw new MojoExecutionException( "There is at least one runner in STOPPED state, execute reset" +
                        " plugin goal or set resetIfStopped to true in your plugin configuration" );
            }
        }
        if( !verifyResult.getStatus() || !verifyResult.getState().equals( State.READY ) ) {
            throw new MojoExecutionException( "Runners could not be verified to run" );
        }

        for( Runner runner : runners ) {
            Result result = client.start( runner );
            if ( ! result.getStatus() ) {
                throw new MojoExecutionException( result.getMessage() );
            }

            getLog().info( "Start request resulted with: " + result.getMessage() );
        }

        getLog().info( "All runners have started!" );
    }


    private void coldRestart( Collection<Instance> instancesToRestart ) throws MojoExecutionException {
        if ( ! coldRestartTomcat ) {
            return;
        }

        getLog().info( "Sending restart tomcat requests to all instances..." );

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
}

