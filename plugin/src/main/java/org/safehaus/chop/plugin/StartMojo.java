package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Collection;

import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.State;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.api.store.amazon.InstanceValues;
import org.safehaus.chop.api.store.amazon.RunnerInstance;
import org.safehaus.chop.client.ChopClient;
import org.safehaus.chop.client.ChopClientModule;
import org.safehaus.chop.client.rest.AsyncRequest;
import org.safehaus.chop.client.rest.StartOp;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.amazonaws.services.ec2.model.Instance;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo ( name = "start", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class StartMojo extends MainMojo {

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
                " milliseconds. until all runners register themselves to the store" );
        ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup,
                runnerKeyPairName, runnerName, endpoint );

        long startTime = System.currentTimeMillis();
        boolean runnersRegistered = false;
        Collection<Runner> runners;
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

        // @todo cold restart here?

        ArrayList<AsyncRequest<RunnerInstance,StartOp>> starts =
                new ArrayList<AsyncRequest<RunnerInstance, StartOp>>( instances.size() );
        for ( Instance instance : instances ) {
            starts.add( new AsyncRequest<RunnerInstance, StartOp>( new RunnerInstance( instance ), new StartOp( instance ) ) );
        }

        try {
            executor.invokeAll( starts );
        }
        catch ( InterruptedException e ) {
            throw new MojoExecutionException( "Failed on start invocations." );
        }

        getLog().info( "All runners have started!" );
    }
}

