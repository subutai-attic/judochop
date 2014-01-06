package org.safehaus.chop.api.store.amazon;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


/**
 * Amazon configuration settings.
 */
public interface AmazonFig extends GuicyFig {

    String AWSKEY_KEY = "aws.s3.key";

    @Key( AmazonFig.AWSKEY_KEY )
    String getAwsKey();



    String AWS_SECRET_KEY = "aws.s3.secret";

    @Key( AmazonFig.AWS_SECRET_KEY )
    String getAwsSecret();



    String AWS_BUCKET_KEY = "aws.s3.bucket";
    String DEFAULT_BUCKET = "perftest-bucket";

    @Default( AmazonFig.DEFAULT_BUCKET )
    @Key( AmazonFig.AWS_BUCKET_KEY )
    String getAwsBucket();



    String SCAN_PERIOD_KEY = "scan.period.milliseconds";
    String DEFAULT_SCAN_PERIOD = "300000";

    @Default( AmazonFig.DEFAULT_SCAN_PERIOD )
    @Key( AmazonFig.SCAN_PERIOD_KEY )
    long getScanPeriod();
}
