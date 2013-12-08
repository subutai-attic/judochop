package org.safehaus.perftest.api.store.amazon;


import com.google.inject.Guice;
import org.junit.Ignore;
import org.junit.Test;
import org.safehaus.perftest.api.store.StoreOperations;


/**
 */
public class S3OperationTest {
    StoreOperations operations = Guice.createInjector( new AmazonStoreModule() ).getInstance( S3Operations.class );

    @Test @Ignore
    public void testRunnersListing() {
        operations.getRunners( new Ec2RunnerInfo().getHostname() );
    }


    @Test @Ignore
    public void testRegister() {
        Ec2RunnerInfo metadata = new Ec2RunnerInfo();
        metadata.setProperty( "foo", "bar" );
        operations.register( metadata );
    }
}
