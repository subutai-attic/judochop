package org.safehaus.chop.api.store.amazon;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.stack.CoordinatedStack;
import org.safehaus.chop.stack.ICoordinatedCluster;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.stack.InstanceSpec;
import org.safehaus.chop.stack.InstanceState;
import org.safehaus.chop.stack.Stack;
import org.safehaus.chop.stack.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.junit.Assume.assumeNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * These tests require some AWS information in order to run.
 *
 * If 'aws.access.key' and 'aws.secret.key' fields are provided in a profile in maven settings.xml file,
 * or if they are directly entered in the config.properties file, these tests are run in the given keys' account.
 *
 * Otherwise, tests are automatically skipped!
 *
 * Other than access and secret keys, your AWS settings has to be compatible with the fields in test-stack.json file;
 * keyName(Key Pair name), imageId (AMI id), ipRuleSet.name (Security Group name) and dataCenter (availability zone)
 * should all be compatible/existent with/in your AWS account.
 */
public class EC2InstanceManagerTest {

    private static final Logger LOG = LoggerFactory.getLogger( EC2InstanceManagerTest.class );

    private static AmazonFig amazonFig;

    private static EC2InstanceManager manager;

    private static CoordinatedStack stack;

    private static Commit commit = mock( Commit.class );
    private static Module module = mock( Module.class );


    @BeforeClass
    public static void setUpData() {
        Injector injector = Guice.createInjector( new AmazonStoreModule() );
        amazonFig = injector.getInstance( AmazonFig.class );

        String accessKey = amazonFig.getAwsAccessKey();
        String secretKey = amazonFig.getAwsSecretKey();

        if( accessKey == null || accessKey.equals( "${aws.access.key}" ) || accessKey.isEmpty() ||
            secretKey == null || secretKey.equals( "${aws.secret.key}" ) || secretKey.isEmpty() ) {

            LOG.warn( "EC2InstanceManagerTest tests are not run, " +
                    "Provided AWS secret or access key values are invalid or no values are provided" );
        }
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream is = EC2InstanceManagerTest.class.getClassLoader().getResourceAsStream( "test-stack.json" );
                Stack basicStack = mapper.readValue( is, Stack.class );

                /** Commit mock object get method values */
                when( commit.getCreateTime() ).thenReturn( new Date() );
                when( commit.getMd5() ).thenReturn( "742e2a76a6ba161f9efb87ce58a9187e" );
                when( commit.getModuleId() ).thenReturn( "778087981" );
                when( commit.getRunnerPath() ).thenReturn( "/some/dummy/path" );
                when( commit.getId() ).thenReturn( "cc471b502aca2791c3a068f93d15b79ff6b7b827" );

                /** Module mock object get method values */
                when( module.getGroupId() ).thenReturn( "org.safehaus.chop" );
                when( module.getArtifactId() ).thenReturn( "chop-maven-plugin" );
                when( module.getVersion() ).thenReturn( "1.0-SNAPSHOT" );
                when( module.getVcsRepoUrl() ).thenReturn( "https://stash.safehaus.org/scm/chop/main.git" );
                when( module.getTestPackageBase() ).thenReturn( "org.safehaus.chop" );
                when( module.getId() ).thenReturn( "778087981" );

                stack = new CoordinatedStack( basicStack, new User( "user", "pass" ), commit, module );
            }
            catch ( Exception e ) {
                LOG.error( "Error while reading test stack json resource", e );
                return;
            }

            manager = injector.getInstance( EC2InstanceManager.class );
        }
    }


    @AfterClass
    public static void cleanup() {

    }


    @Before
    public void checkCredentialsExist() {
        assumeNotNull( manager );
    }


    @Test
    public void testCluster() {

        ICoordinatedCluster cluster = stack.getClusters().get( 0 );
        LOG.info( "Launching cluster {}'s {} instances...", cluster.getName(), cluster.getSize()  );

        manager.launchCluster( stack,  stack.getClusters().get( 0 ), 100000 );

        Collection<Instance> instances = manager.getClusterInstances( stack, cluster );

        assertEquals( "Number of launched instances is different than expected", cluster.getSize(), instances.size() );

        LOG.info( "Instances are successfully launched, now terminating..." );

        Collection<String> instanceIds = new ArrayList<String>( instances.size() );
        for( Instance i : instances ) {
            instanceIds.add( i.getId() );
        }

        manager.terminateInstances( instanceIds );
        boolean terminated = manager.waitUntil( instanceIds, InstanceState.ShuttingDown, 100000 );

        if( ! terminated ) {
            instances = manager.getClusterInstances( stack, cluster );
            assertEquals( "Some instances could not be terminated! You may need to manually terminate the instances",
                    0, instances.size() );
        }
    }


    @Test
    public void testRunners() {

        int instanceCount = 2;
        InstanceSpec iSpec = stack.getClusters().get( 0 ).getInstanceSpec();
        manager.launchRunners( stack, iSpec, instanceCount, 100000 );

        Collection<Instance> instances = manager.getRunnerInstances( stack );

        assertEquals( "Number of launched instances is different than expected", instanceCount, instances.size() );

        LOG.info( "Instances are successfully launched, now terminating..." );

        Collection<String> instanceIds = new ArrayList<String>( instances.size() );
        for( Instance i : instances ) {
            instanceIds.add( i.getId() );
        }

        manager.terminateInstances( instanceIds );
        boolean terminated = manager.waitUntil( instanceIds, InstanceState.ShuttingDown, 100000 );

        if( ! terminated ) {
            instances = manager.getRunnerInstances( stack );
            assertEquals( "Some instances could not be terminated! You may need to manually terminate the instances",
                    0, instances.size() );
        }

    }


}
