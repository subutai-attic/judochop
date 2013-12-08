package org.safehaus.perftest.api.store.amazon;


import com.google.inject.Guice;
import org.junit.Test;
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
        operations.getRunners( new Ec2RunnerInfo() );
    }


    @Test
    public void testRegister() {
        Ec2RunnerInfo metadata = new Ec2RunnerInfo();
        metadata.setProperty( "foo", "bar" );
        operations.register( metadata );
    }
}
