package org.safehaus.perftest.api.store.amazon;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.safehaus.perftest.api.PerftestApiModule;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.store.StoreOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Guice;


/** Tests the S3Operations */
public class S3OperationsTest {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );
    StoreOperations operations =
            Guice.createInjector( new AmazonStoreModule(), new PerftestApiModule() ).getInstance( S3Operations.class );

    private String accessKey = System.getProperty( "accessKey" );
    private String secretKey = System.getProperty( "secretKey" );
    private String amiID = System.getProperty( "amiID" );
    private String securityGroup = System.getProperty( "securityGroup" );
    private String keyName = System.getProperty( "keyName" );
    private String runnerName = "perftest-runner";

    @Test
    public void testRunnersListing() {
        Map<String, RunnerInfo> runners = operations.getRunners( new Ec2RunnerInfo() );

        for ( RunnerInfo runnerInfo : runners.values() ) {
            LOG.debug( "Got runner {}", runnerInfo );
        }
    }


    @Test
    public void testRegister() {
        Ec2RunnerInfo metadata = new Ec2RunnerInfo();
        metadata.setProperty( "foo", "bar" );
        operations.register( metadata );
    }

    @Test
    public void testDeleteGhostRunners() {
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances( runnerName, InstanceStateName.Running );
        Collection<String> instanceHosts = new ArrayList<String>( instances.size() );
        for ( Instance i : instances ) {
            instanceHosts.add( i.getPublicDnsName() );
        }
        operations.deleteGhostRunners( instanceHosts );

    }
}
