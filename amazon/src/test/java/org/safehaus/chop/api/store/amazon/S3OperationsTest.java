package org.safehaus.chop.api.store.amazon;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.store.StoreOperations;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Guice;
import com.google.inject.Inject;


/** Tests the S3Operations */
@RunWith( JukitoRunner.class )
public class S3OperationsTest {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );
    StoreOperations operations = Guice.createInjector( new AmazonStoreModule() ).getInstance( S3Operations.class );

    private String accessKey = System.getProperty( "accessKey" );
    private String secretKey = System.getProperty( "secretKey" );
    private String amiID = System.getProperty( "amiID" );
    private String securityGroup = System.getProperty( "securityGroup" );
    private String keyName = System.getProperty( "keyName" );


    @Inject
    public Runner runner;


    @Test
    public void testRunnersListing() {
        Map<String, Runner> runners = operations.getRunners( runner );

        for ( Runner runner : runners.values() ) {
            LOG.debug( "Got runner {}", runner );
        }
    }


    @Test
    public void testRegister() {
        runner.override( "foo", "bar" );
        runner.override( Runner.HOSTNAME_KEY, "foobar-host" );
        operations.register( runner );
    }


    @Test
    public void testDeleteGhostRunners() {
        final String runnerName = "chop-runner";
        EC2Manager ec2 = new EC2Manager( accessKey, secretKey, amiID, securityGroup, keyName, runnerName );
        Collection<Instance> instances = ec2.getInstances( runnerName, InstanceStateName.Running );
        Collection<String> instanceHosts = new ArrayList<String>( instances.size() );
        for ( Instance i : instances ) {
            instanceHosts.add( i.getPublicDnsName() );
        }
        operations.deleteGhostRunners( instanceHosts );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class TestModule extends JukitoModule {
        @Override
        protected void configureTest() {
            install( new GuicyFigModule( Runner.class) );
        }
    }
}
