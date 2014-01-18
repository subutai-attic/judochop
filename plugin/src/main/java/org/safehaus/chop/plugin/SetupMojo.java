package org.safehaus.chop.plugin;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.safehaus.chop.api.ChopUtils;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.Store;
import org.safehaus.chop.api.store.amazon.AmazonStoreModule;
import org.safehaus.chop.api.store.amazon.EC2Manager;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;
import com.google.inject.Guice;
import com.google.inject.Injector;


@Mojo( name = "setup" )
public class SetupMojo extends MainMojo {


    protected SetupMojo( MainMojo mojo ) {
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
        this.setupCreatedInstances = mojo.setupCreatedInstances;
        this.sleepAfterCreation = mojo.sleepAfterCreation;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected SetupMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        initCertStore();

        try {
            EC2Manager ec2Manager = new EC2Manager( accessKey, secretKey, amiID, awsSecurityGroup, runnerKeyPairName,
                    runnerName, endpoint );

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

            Injector injector = Guice.createInjector( new AmazonStoreModule() );
            Store store = injector.getInstance( Store.class );
            Collection<Instance> activeInstances = ec2Manager.getInstances( runnerName, InstanceStateName.Running );

            Set<String> activeInstanceHostnames = new HashSet<String>( activeInstances.size() );
            for ( Instance instance : activeInstances ) {
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
