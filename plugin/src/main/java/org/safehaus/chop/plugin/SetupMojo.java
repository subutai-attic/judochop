package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.Store;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.api.store.amazon.InstanceValues;
import org.safehaus.chop.client.ssh.AsyncSsh;
import org.safehaus.chop.client.ssh.TomcatRestart;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "setup" )
public class SetupMojo extends MainMojo {

    protected SetupMojo( MainMojo mojo ) {
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
    protected SetupMojo() {
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
        initCertStore();
        setSshValues();

        try {
            if ( ec2Manager == null ) {
                ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup, runnerKeyPairName,
                    runnerName, endpoint );
            }

            if( instanceType != null && ! instanceType.isEmpty() ) {
                try {
                    ec2Manager.setDefaultType( InstanceType.fromValue( instanceType ) );
                }
                catch ( IllegalArgumentException iae ) {
                    getLog().warn( "Instance type value " + instanceType +
                            " does not correspond to a valid type string like any of the following:\n\t" +
                            Arrays.toString( InstanceType.values() ) );
                }
            }
            ec2Manager.setDefaultTimeout( setupTimeout );

            if( availabilityZone != null && ! availabilityZone.isEmpty() ) {
                ec2Manager.setAvailabilityZone( availabilityZone );
            }

            if ( ec2Manager.ensureRunningInstances( runnerCount ) > 0 ) {
                super.setupCreatedInstances = true;
            }

            int port = Integer.parseInt( Runner.DEFAULT_SERVER_PORT );
            ArrayList<Integer> ports = new ArrayList<Integer>();
            ports.add( port );
            ec2Manager.updateSecurityGroupRecords( ports, false );
            if( securityGroupExceptions != null && securityGroupExceptions.size() != 0 ) {
                Collection<IpPermission> existingRules = ec2Manager.getSecurityGroupRecords();
                String record;
                ArrayList<String> ipRanges = new ArrayList<String>();
                for( Object obj : securityGroupExceptions ) {
                    record = obj.toString();
                    int colonInd = record.indexOf( ':' );
                    ipRanges.clear();
                    ipRanges.add( record.substring( 0, colonInd ) );
                    port = Integer.parseInt( record.substring( colonInd + 1 ) );

                    boolean permissionAlreadyGranted = false;
                    for( IpPermission permission : existingRules ) {
                        if( permission.getFromPort() <= port &&  permission.getToPort() >= port ) {
                            // This doesn't check if given IP range is already covered in an existing range,
                            // but checks if there is another record with the same exact range
                            // e.g. If there is a rule with 10.11.12.0/24, it also covers 10.11.12.123/32
                            for( String range : permission.getIpRanges() ) {
                                if ( range.equals( ipRanges.get( 0 ) ) ) {
                                    permissionAlreadyGranted = true;
                                    break;
                                }
                            }
                        }
                    }

                    if( ! permissionAlreadyGranted ) {
                        ec2Manager.addRecordToSecurityGroup( ipRanges, "tcp", port );
                    }
                }
            }


            if ( instances == null ) {
                instances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );
            }

            if ( executor == null ) {
                //executor = Executors.newCachedThreadPool();
                executor = Executors.newFixedThreadPool( 100 );
            }

            getLog().info( "" );
            getLog().info( "------------------------------------------------------------------------" );
            getLog().info( "Waiting for all runner ssh daemons to come up." );
            getLog().info( "------------------------------------------------------------------------" );
            getLog().info( "" );

            boolean allUp;
            Collection<AsyncSsh<Instance>> failed = AsyncSsh.getCommands( instances, list );

            do {
                getLog().info( "Sleeping for 3 seconds" );
                Thread.sleep( 3000 );
                getLog().info( "Woke up from sleep ready to parallel execute ssh ls commands." );

                try {
                    getLog().info( "About to execute " + failed.size() + " commands." );

                    executor.invokeAll( failed );

                    failed = AsyncSsh.extractFailures( failed );
                    getLog().info( failed.size() + " of those commands failed." );

                    if ( failed.size() > 0 ) {
                        getLog().info( "Failed on ssh commands: " );
                        for ( AsyncSsh<Instance> command : failed ) {
                            getLog().info( "\t\t" + command );
                        }
                    }

                    allUp = failed.isEmpty();
                }
                catch ( Exception e ) {
                    getLog().error( "Got the following error during command execution.", e );
                    allUp = false;
                }
            } while ( ! allUp );


            getLog().info( "" );
            getLog().info( "------------------------------------------------------------------------" );
            getLog().info( "Sending admin password reset and restart command to Tomcat instances ..." );
            getLog().info( "------------------------------------------------------------------------" );
            getLog().info( "" );


            Collection<TomcatRestart<Instance>> failedTomcat = TomcatRestart.getCommands( managerAppPassword, instances, list );
            do {
                getLog().info( "Sleeping for 3 seconds" );
                Thread.sleep( 3000 );
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

//            List<SSHRequestThread> restarters = new ArrayList<SSHRequestThread>( instances.size() );
//
//            for ( Instance instance : instances ) {
//                SSHRequestThread restarter = new SSHRequestThread();
//                restarter.setSshKeyFile( runnerSSHKeyFile );
//                restarter.setInstanceURL( instance.getPublicDnsName() );
//                restarter.setTomcatAdminPassword( managerAppPassword );
//                restarter.setInstance( instance );
//                restarters.add( restarter );
//                restarter.start();
//            }
//
//            for ( SSHRequestThread restarter : restarters ) {
//                try {
//                    restarter.join( 40000 ); // Is this enough or too much, should this be an annotated parameter?
//                }
//                catch ( InterruptedException e ) {
//                    getLog().warn( "Restart request on " + restarter.getInstance().getPublicDnsName()
//                            + " is interrupted before finish", e );
//                }
//            }
//
//            ResponseInfo response;
//            boolean failedRestart = false;
//            for ( SSHRequestThread restarter : restarters ) {
//                response = restarter.getResult();
//                if ( !response.isRequestSuccessful() || !response.isOperationSuccessful() ) {
//                    for ( String s : response.getMessages() ) {
//                        getLog().info( s );
//                    }
//                    for ( String s : response.getErrorMessages() ) {
//                        getLog().info( s );
//                    }
//                    failedRestart = true;
//                }
//            }
//
//            if ( failedRestart ) {
//                throw new MojoExecutionException(
//                        "There are instances that failed to restart properly, verify the cluster before moving on" );
//            }

            getLog().info( "Each Runner's Tomcat admin password has been reset and Tomcat has been restarted." );


            // ---

            Set<String> activeInstanceHostnames = new HashSet<String>( instances.size() );
            for ( Instance instance : instances ) {
                activeInstanceHostnames.add( instance.getPublicDnsName() );
            }
            store.deleteGhostRunners( activeInstanceHostnames );

            for ( String hostname : activeInstanceHostnames ) {
                try {
                    ChopUtils.installRunnerKey( null, hostname );
                }
                catch ( Exception e ) {
                    getLog().error( "Failed to install runner key.", e );
                }
            }
        }
        catch ( Exception e ) {
            throw new MojoExecutionException( "Error in setup cluster", e );
        }
    }


    protected void initCertStore() {
        if ( ChopUtils.isStoreInitialized() ) {
            return;
        }

        try {
            if ( certStorePassphrase == null ) {
                ChopUtils.installCert( endpoint, 443, null );
            }
            else {
                ChopUtils.installCert( endpoint, 443, certStorePassphrase.toCharArray() );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
