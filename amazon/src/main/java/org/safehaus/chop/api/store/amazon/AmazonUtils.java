package org.safehaus.chop.api.store.amazon;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;


public class AmazonUtils {

    /**
     * @param accessKey
     * @param secretKey
     * @return
     */
    public static AmazonEC2Client getEC2Client( String accessKey, String secretKey ) {
        AWSCredentialsProvider provider;
        if ( accessKey != null && secretKey != null ) {
            AWSCredentials credentials = new BasicAWSCredentials( accessKey, secretKey );
            provider = new StaticCredentialsProvider( credentials );
        }
        else {
            provider = new DefaultAWSCredentialsProviderChain();
        }

        AmazonEC2Client client = new AmazonEC2Client( provider );

        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setProtocol( Protocol.HTTPS );
        client.setConfiguration( configuration );
        return client;
    }


    public static String getEndpoint( String availabilityZone ) {
        // see http://docs.aws.amazon.com/general/latest/gr/rande.html#ec2_region
        if ( availabilityZone != null ) {
            if ( availabilityZone.contains( "us-east-1" ) ) {
                return "ec2.us-east-1.amazonaws.com";
            }
            else if ( availabilityZone.contains( "us-west-1" ) ) {
                return "ec2.us-west-1.amazonaws.com";
            }
            else if ( availabilityZone.contains( "us-west-2" ) ) {
                return "ec2.us-west-2.amazonaws.com";
            }
            else if ( availabilityZone.contains( "eu-west-1" ) ) {
                return "ec2.eu-west-1.amazonaws.com";
            }
            else if ( availabilityZone.contains( "ap-southeast-1" ) ) {
                return "ec2.ap-southeast-1.amazonaws.com";
            }
            else if ( availabilityZone.contains( "ap-southeast-2" ) ) {
                return "ec2.ap-southeast-2.amazonaws.com";
            }
            else if ( availabilityZone.contains( "ap-northeast-1" ) ) {
                return "ec2.ap-northeast-1.amazonaws.com";
            }
            else if ( availabilityZone.contains( "sa-east-1" ) ) {
                return "ec2.sa-east-1.amazonaws.com";
            }
            else {
                return "ec2.us-east-1.amazonaws.com";
            }
        }
        else {
            return "ec2.us-east-1.amazonaws.com";
        }
    }
}
