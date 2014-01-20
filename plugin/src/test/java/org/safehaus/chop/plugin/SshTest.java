package org.safehaus.chop.plugin;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.safehaus.chop.api.store.amazon.EC2Manager;
import org.safehaus.chop.client.ssh.AsyncSsh;
import org.safehaus.chop.api.SshValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;

import static junit.framework.TestCase.assertTrue;


/**
 * Some tests with Asynchronous Ssh commands.
 */
public class SshTest {
    private static final Logger LOG = LoggerFactory.getLogger( SshTest.class );
    private static Map<String, AsyncSsh<Instance>> instanceMap = new HashMap<String, AsyncSsh<Instance>>();
    private static Collection<Instance> instances;
    private static final String sshKeyFile = System.getProperty( "runner.ssh.key.file" );
    private static ExecutorService service;
    private static SshValues<Instance> values = new SshValues<Instance>() {
                        @Override
                        public String getHostname( final Instance associate ) {
                            return associate.getPublicDnsName();
                        }


                        @Override
                        public String getCommand( final Instance associate ) {
                            return "ls";
                        }


                        @Override
                        public String getSshKeyFile( final Instance associate ) {
                            return sshKeyFile;
                        }
                    };


    @BeforeClass
    public static void setup() {
        final EC2Manager manager =
                new EC2Manager( System.getProperty( "aws.s3.key" ), System.getProperty( "aws.s3.secret" ),
                        System.getProperty( "ami.id" ), System.getProperty( "security.group" ),
                        System.getProperty( "runner.keypair.name" ), System.getProperty( "runner.name" ),
                        "ec2.us-east-1.amazonaws.com" );

        instances = manager.getInstances( System.getProperty( "runner.name" ), InstanceStateName.Running );

        for ( Instance instance : instances ) {
            instanceMap.put( instance.getPublicDnsName(),
                    new AsyncSsh<Instance>( "ls", sshKeyFile, instance.getPublicDnsName() )
                            .setAssociate( instance ) );
        }

        service = Executors.newFixedThreadPool( instances.size() );
    }


    @AfterClass
    public static void tearDown() {
        service.shutdown();
    }


    @Test
    public void testWithExecutor() throws InterruptedException {
        if ( instances.size() == 0 ) {
            return;
        }
        long startTime = System.currentTimeMillis();
        service.invokeAll( instanceMap.values() );
        assertTrue( isSuccessful( instanceMap.values() ) );
        LOG.debug( "Total time = {} milliseconds!", System.currentTimeMillis() - startTime );
    }


    @Test
    public void testGetCommands() throws InterruptedException {
        if ( instances.size() == 0 ) {
            return;
        }
        Collection<AsyncSsh<Instance>> commands = AsyncSsh.getCommands( instances, values );
        long startTime = System.currentTimeMillis();
        service.invokeAll( commands );
        assertTrue( isSuccessful( commands ) );
        LOG.debug( "Total time = {} milliseconds!", System.currentTimeMillis() - startTime );
    }


    @Test
    public void testExecute() throws InterruptedException {
        if ( instances.size() == 0 ) {
            return;
        }
        long startTime = System.currentTimeMillis();
        assertTrue( AsyncSsh.execute( instances, values ) );
        LOG.debug( "Total time = {} milliseconds!", System.currentTimeMillis() - startTime );
    }


    public static boolean isSuccessful( Collection<? extends AsyncSsh<?>> commands ) {
        for ( AsyncSsh<?> ssh : commands ) {
            if ( ! ssh.isSuccess() ) {
                return false;
            }
        }

        return true;
    }
}
