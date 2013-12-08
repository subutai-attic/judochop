package org.safehaus.perftest.amazon;


import com.google.inject.Guice;
import org.junit.Ignore;
import org.junit.Test;


/**
 */
public class S3OperationTest {
    S3Operations operations = Guice.createInjector( new AmazonS3Module() ).getInstance( S3Operations.class );

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
