package org.safehaus.chop.api.store.amazon;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.StoreService;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Inject;

import static junit.framework.TestCase.assertNotNull;


/** Tests the Amazon based PerftestStore implementation. */
@RunWith( JukitoRunner.class )
@UseModules( AmazonStoreModule.class )
public class AmazonS3ServiceAwsImplTest {
    private static final Logger LOG = LoggerFactory.getLogger( AmazonS3ServiceAwsImplTest.class );
    @Inject
    StoreService service;


    @Before
    public void setup() {
        service.start();
    }


    @After
    public void tearDown() {
        service.stop();
    }


    @Test
    public void testGetRunners() {
        Map<String, Runner> runners = service.getRunners();
        assertNotNull( runners );
        int runnerCount = 0;

        for ( Runner runner : runners.values() ) {
            runnerCount++;
            LOG.debug( "Got runner {}", runner );
        }

        if ( runnerCount == 0 ) {
            LOG.warn( "Not much of a test if we got no drivers" );
        }
    }


    private String accessKey = System.getProperty( "accessKey" );
    private String secretKey = System.getProperty( "secretKey" );
    private String amiID = System.getProperty( "amiID" );
    private String securityGroup = System.getProperty( "securityGroup" );
    private String keyName = System.getProperty( "keyName" );


    @Inject
    public Runner runner;


    @Test
    public void testRunnersListing() {
        Map<String, Runner> runners = service.getRunners( runner );

        for ( Runner runner : runners.values() ) {
            LOG.debug( "Got runner {}", runner );
        }
    }


    @Test
    public void testRegister() {
        runner.override( "foo", "bar" );
        runner.override( Runner.HOSTNAME_KEY, "foobar-host" );
        service.register( runner );
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
        service.deleteGhostRunners( instanceHosts );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class TestModule extends JukitoModule {
        @Override
        protected void configureTest() {
            install( new GuicyFigModule( Runner.class) );
        }
    }
}
