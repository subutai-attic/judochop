package org.safehaus.perftest.api.store.amazon;


import java.util.Map;

import com.google.inject.Guice;
import org.junit.Test;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.store.StoreOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the S3Operations
 */
public class S3OperationsTest {
    private static final Logger LOG = LoggerFactory.getLogger( S3Operations.class );
    StoreOperations operations = Guice.createInjector( new AmazonStoreModule() ).getInstance( S3Operations.class );

    @Test
    public void testRunnersListing() {
        Map<String,RunnerInfo> runners = operations.getRunners( new Ec2RunnerInfo() );

        for ( RunnerInfo runnerInfo : runners.values() )
        {
            LOG.debug( "Got runner {}", runnerInfo );
        }
    }


    @Test
    public void testRegister() {
        Ec2RunnerInfo metadata = new Ec2RunnerInfo();
        metadata.setProperty( "foo", "bar" );
        operations.register( metadata );
    }
}
