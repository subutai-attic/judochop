/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 5:40 PM
 */
package org.safehaus.chop.api.store.amazon;


import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.store.StoreOperations;
import org.safehaus.chop.api.store.StoreService;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


public class AmazonStoreModule extends AbstractModule implements ConfigKeys {
    private static final Logger LOG = LoggerFactory.getLogger( AmazonStoreModule.class );
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        install( new GuicyFigModule( Runner.class ) );
        bind( StoreOperations.class ).to( S3Operations.class );
        bind( StoreService.class ).to( AmazonS3ServiceAwsImpl.class );
    }


    @Provides
    AmazonS3Client provideAmazonS3Client() {
        AmazonS3Client client;

        AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return getAwsKeyProperty().get();
            }


            @Override
            public String getAWSSecretKey() {
                return getAwsSecretProperty().get();
            }
        };

        client = new AmazonS3Client( credentials );
        return client;
    }


    @Provides
    @Named( AWSKEY_KEY )
    DynamicStringProperty getAwsKeyProperty() {
        DynamicStringProperty prop = propertyFactory.getStringProperty( AWSKEY_KEY, "AWS_KEY_NOT_SET" );
        LOG.debug( "{} = {} ", AWSKEY_KEY, prop.get() );
        return prop;
    }


    @Provides
    @Named( AWS_SECRET_KEY )
    DynamicStringProperty getAwsSecretProperty() {
        DynamicStringProperty prop = propertyFactory.getStringProperty( AWS_SECRET_KEY, "AWS_SECRET_NOT_SET" );
        LOG.debug( "{} = {}", AWS_SECRET_KEY, prop.get() );
        return prop;
    }


    @Provides
    @Named( AWS_BUCKET_KEY )
    DynamicStringProperty getAwsBucketProperty() {
        return propertyFactory.getStringProperty( AWS_BUCKET_KEY, DEFAULT_BUCKET );
    }


    @Provides
    @Named( SCAN_PERIOD_KEY )
    DynamicLongProperty getScanPeriodProperty() {
        return propertyFactory.getLongProperty( SCAN_PERIOD_KEY, DEFAULT_SCAN_PERIOD );
    }
}
