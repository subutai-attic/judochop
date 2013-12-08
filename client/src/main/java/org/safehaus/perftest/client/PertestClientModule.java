/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 1:48 PM
 */
package org.safehaus.perftest.client;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


public class PertestClientModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        bind( PerftestClient.class ).to( PerftestClientImpl.class );
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
        return propertyFactory.getStringProperty( AWSKEY_KEY, "AWS_KEY_NOT_SET" );
    }


    @Provides
    @Named( AWS_SECRET_KEY )
    DynamicStringProperty getAwsSecretProperty() {
        return propertyFactory.getStringProperty( AWS_SECRET_KEY, "AWS_SECRET_NOT_SET" );
    }


    @Provides
    @Named( AWS_BUCKET_KEY )
    DynamicStringProperty getAwsBucketProperty() {
        return propertyFactory.getStringProperty( AWS_BUCKET_KEY, DEFAULT_BUCKET );
    }
}
