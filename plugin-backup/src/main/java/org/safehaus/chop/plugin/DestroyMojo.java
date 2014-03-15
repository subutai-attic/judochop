package org.safehaus.chop.plugin;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.safehaus.chop.api.store.amazon.EC2Manager;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;


@Mojo ( name = "destroy" )
public class DestroyMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {
        EC2Manager ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup,
                runnerKeyPairName, runnerName, endpoint );
        Collection<Instance> instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );

        if ( instances.size() == 0 ) {
            getLog().info( "No running " + runnerName + " instances to destroy." );
            return;
        }

        Set<String> instanceIds = new HashSet<String>( instances.size() );

        for ( Instance instance : instances ) {
            instanceIds.add( instance.getInstanceId() );
        }

        TerminateInstancesResult results = ec2Manager.terminateEC2Instances( instanceIds );
        for ( InstanceStateChange stateChange : results.getTerminatingInstances() ) {
            if ( stateChange.getCurrentState().getName().equals( InstanceStateName.ShuttingDown.toString() ) ) {
                getLog().info( "Shutting down Runner instance " + stateChange.getInstanceId() );
                instanceIds.remove( stateChange.getInstanceId() );
            }
        }

        if ( instanceIds.size() > 0 ) {
            getLog().warn( "Some instances will remaining running: " + instanceIds );
        }
        else {
            getLog().info( "All Runners are being terminated!" );
        }
    }
}

