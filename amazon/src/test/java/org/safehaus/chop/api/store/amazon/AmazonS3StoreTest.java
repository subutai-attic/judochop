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
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.StoreService;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.google.inject.Guice;
import com.google.inject.Inject;

import static junit.framework.TestCase.assertNotNull;


/** Tests the Amazon based PerftestStore implementation. */
@RunWith( JukitoRunner.class )
@UseModules( AmazonStoreModule.class )
public class AmazonS3StoreTest {
    private static final Logger LOG = LoggerFactory.getLogger( AmazonS3StoreTest.class );
    private String accessKey = System.getProperty( "accessKey" );
    private String secretKey = System.getProperty( "secretKey" );
    private String amiID = System.getProperty( "amiID" );
    private String securityGroup = System.getProperty( "securityGroup" );
    private String keyName = System.getProperty( "keyName" );


    public RunnerFig runnerFig;


    @Inject
    StoreService service;


    @Before
    public void setup() {
        runnerFig = Guice.createInjector( new GuicyFigModule( RunnerFig.class ) ).getInstance( RunnerFig.class );
        service.start();
    }


    @After
    public void tearDown() {
        runnerFig = null;
        service.stop();
    }


    @Test
    public void testGetRunners() {
        Map<String, RunnerFig> runners = service.getRunners();
        assertNotNull( runners );
        int runnerCount = 0;

        for ( RunnerFig runnerFig : runners.values() ) {
            runnerCount++;
            LOG.debug( "Got runnerFig {}", runnerFig );
        }

        if ( runnerCount == 0 ) {
            LOG.warn( "Not much of a test if we got no drivers" );
        }
    }


    @Test
    public void testRunnersListing() {
        Map<String, RunnerFig> runners = service.getRunners( runnerFig );

        for ( RunnerFig runnerFig : runners.values() ) {
            LOG.debug( "Got runnerFig {}", runnerFig );
        }
    }


    @Test
    public void testRegister() {
        runnerFig.bypass( RunnerFig.HOSTNAME_KEY, "foobar-host" );
        service.register( runnerFig );
    }


    @Test
    public void testDeleteGhostRunners() {
        final String runnerName = "chop-runnerFig";
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
            install( new GuicyFigModule( RunnerFig.class) );
        }
    }
}
