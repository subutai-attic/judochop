package org.apache.usergrid.chop.webapp.dao.model;


import org.apache.usergrid.chop.api.ProviderParams;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;


public class BasicProviderParams implements ProviderParams {

    private String username;
    private String instanceType;
    private String availabilityZone;
    private String accessKey;
    private String secretKey;
    private String imageId;
    private String securityGroup;
    private String runnerName;
    private Map<String, String> keys = new HashMap<String, String>();

    public BasicProviderParams ( String username ) {
        this(username, "", "", "", "", "", "", "");
    }

    public BasicProviderParams ( String username, String instanceType, String availabilityZone, String accessKey,
                                 String secretKey, String imageId, String securityGroup, String runnerName ) {

        this.username = username;
        this.instanceType = instanceType;
        this.availabilityZone = availabilityZone;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.imageId = imageId;
        this.securityGroup = securityGroup;
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
    public String getRunnerName() {
        return runnerName;
    }

    @Override
    public Map<String, String> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
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
                .append( "runnerName", runnerName )
                .append( "keys", keys )
                .toString();
    }
}
