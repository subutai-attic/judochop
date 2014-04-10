package org.apache.usergrid.chop.api.store.amazon;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


/**
 * Amazon configuration settings.
 */
@FigSingleton
public interface AmazonFig extends GuicyFig {

    String AWS_ACCESS_KEY = "aws.access.key";

    @Key( AmazonFig.AWS_ACCESS_KEY )
    String getAwsAccessKey();



    String AWS_SECRET_KEY = "aws.secret.key";

    @Key( AmazonFig.AWS_SECRET_KEY )
    String getAwsSecretKey();


    String AWS_S3_KEY = "aws.s3.key";

    @Key( AmazonFig.AWS_S3_KEY )
    String getAwsKey();


    String AWS_S3_SECRET = "aws.s3.secret";

    @Key( AmazonFig.AWS_S3_SECRET )
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
