package org.safehaus.chop.webapp.dao.model;


import org.safehaus.chop.api.ProviderParams;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public class BasicProviderParams implements ProviderParams {

    private String username;
    private String instanceType;
    private String availabilityZone;
    private String accessKey;
    private String secretKey;
    private String imageId;
    private String securityGroup;
    private String keyPairName;
    private String runnerName;


    public BasicProviderParams ( String username ) {
        this.username = username;
    }


    public BasicProviderParams ( String username, String instanceType, String availabilityZone, String accessKey,
                                 String secretKey, String imageId, String securityGroup, String keyPairName,
                                 String runnerName ) {

        this.username = username;
        this.instanceType = instanceType;
        this.availabilityZone = availabilityZone;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.imageId = imageId;
        this.securityGroup = securityGroup;
        this.keyPairName = keyPairName;
        this.runnerName = runnerName;
    }


    public void setInstanceType( final String instanceType ) {
        this.instanceType = instanceType;
    }


    public void setAvailabilityZone( final String availabilityZone ) {
        this.availabilityZone = availabilityZone;
    }


    public void setAccessKey( final String accessKey ) {
        this.accessKey = accessKey;
    }


    public void setSecretKey( final String secretKey ) {
        this.secretKey = secretKey;
    }


    public void setImageId( final String imageId ) {
        this.imageId = imageId;
    }


    public void setSecurityGroup( final String securityGroup ) {
        this.securityGroup = securityGroup;
    }


    public void setKeyPairName( final String keyPairName ) {
        this.keyPairName = keyPairName;
    }


    public void setRunnerName( final String runnerName ) {
        this.runnerName = runnerName;
    }


    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public String getInstanceType() {
        return instanceType;
    }


    @Override
    public String getAvailabilityZone() {
        return availabilityZone;
    }


    @Override
    public String getAccessKey() {
        return accessKey;
    }


    @Override
    public String getSecretKey() {
        return secretKey;
    }


    @Override
    public String getImageId() {
        return imageId;
    }


    @Override
    public String getSecurityGroup() {
        return securityGroup;
    }


    @Override
    public String getKeyPairName() {
        return keyPairName;
    }


    @Override
    public String getRunnerName() {
        return runnerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("user", username)
                .append( "instanceType", instanceType )
                .append( "availabilityZone", availabilityZone )
                .append( "accessKey", accessKey )
                .append( "secretKey", secretKey )
                .append( "imageId", imageId )
                .append( "securityGroup", securityGroup )
                .append( "keyPairName", keyPairName )
                .append( "runnerName", runnerName )
                .toString();
    }
}
