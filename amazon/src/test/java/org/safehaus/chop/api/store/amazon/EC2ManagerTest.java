package org.safehaus.chop.api.store.amazon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.IpPermission;

import junit.framework.Assert;


public class EC2ManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger( EC2Manager.class );

    private String accessKey = System.getProperty( "accessKey" );
    private String secretKey = System.getProperty( "secretKey" );
    private String amiID = System.getProperty( "amiID" );
    private String securityGroup = System.getProperty( "securityGroup" );
    private String keyName = System.getProperty( "keyName" );
    private String runnerName = "chop-runner";

    @Test @Ignore
    public void testLaunchInstances()
    {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        ec2.setAvailabilityZone( "us-east-1d" );
        ec2.launchEC2Instances( InstanceType.M1Medium, 2 );
        ec2.close();
    }

    @Test @Ignore
    public void testGetInstances1()
    {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances();
        for ( Instance i : instances ) {
            LOG.info("Instance {} {}", i.getInstanceId(), i.getState().getName().toString() );
        }
        ec2.close();
    }

    @Test @Ignore
    public void testGetInstances2()
    {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances( runnerName );
        for ( Instance i : instances ) {
            LOG.info("Instance {} {}", i.getInstanceId(), i.getState().getName().toString() );
        }
        ec2.close();
    }

    @Test @Ignore
    public void testGetInstances3()
    {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances( InstanceStateName.Running );
        for ( Instance i : instances ) {
            LOG.info("Instance {} {}", i.getInstanceId(), i.getState().getName().toString() );
        }
        ec2.close();
    }

    @Test @Ignore
    public void testGetInstances4()
    {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances( runnerName, InstanceStateName.Running );
        for ( Instance i : instances ) {
            LOG.info("Instance {} {}", i.getInstanceId(), i.getState().getName().toString() );
        }
        ec2.close();
    }

    @Test @Ignore
    public void testAddRecordToSecurityGroup() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<String> ipRanges = new ArrayList<String>();
        ipRanges.add( "213.74.31.18/32" );
        ec2.deleteSecurityGroupRecord( ipRanges, "tcp", 8080 );
        ec2.addRecordToSecurityGroup( ipRanges, "tcp", 8080 );
        ec2.close();
    }

    @Test @Ignore
    public void testDeleteSecurityGroupRecord() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<String> ipRanges = new ArrayList<String>();
        ipRanges.add( "10.235.6.227/32" );
        ec2.deleteSecurityGroupRecord( ipRanges, "tcp", 80 );
        ec2.close();
    }

    @Test @Ignore
    public void testGetSecurityGroupRecords() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<IpPermission> permissions = ec2.getSecurityGroupRecords();
        for( IpPermission permission : permissions ) {
            LOG.info( "Port: {} = {}", permission.getFromPort(), permission.getToPort() );
            for( String iprange : permission.getIpRanges() ) {
                LOG.info( "Ip range: {}", iprange );
            }
        }
        ec2.close();
    }

    @Test @Ignore
    public void testUpdateSecurityGroupRecords() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Integer> ports = new ArrayList<Integer> ( 2 );
        ports.add( 8080 );
        ec2.updateSecurityGroupRecords( ports, false );
        ec2.close();
    }


    @Test @Ignore
    public void testEnsureRunningInstancesMin() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        boolean res = ec2.ensureRunningInstancesMin( 4 );
        LOG.info( "Result: ", res );
        ec2.close();
    }

    @Test @Ignore
    public void testEnsureRunningInstancesMax() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        boolean res = ec2.ensureRunningInstancesMax( 1 );
        LOG.info( "Result: " + res );
        ec2.close();
    }

    @Test @Ignore
    public void testEnsureRunningInstances() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        boolean res = ec2.ensureRunningInstances( 3, 5 );
        LOG.info( "Result: " + res );
        ec2.close();
    }

    @Test @Ignore
    public void testListSecurityGroups() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<String> groups = ec2.listSecurityGroups();
        LOG.info( "Security Groups are:" );
        for( String g : groups ) {
            LOG.info( g );
        }
        ec2.close();
    }

    @Test @Ignore
    public void testCreateDeleteSecurityGroup() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        String groupName = "perftest-security-create-delete-test";
        if( ec2.securityGroupExists( groupName ) ) {
            ec2.deleteSecurityGroup( groupName );
        }
        Assert.assertTrue( ec2.createSecurityGroup( groupName ) && ec2.deleteSecurityGroup( groupName ) );
        ec2.close();
    }
}
