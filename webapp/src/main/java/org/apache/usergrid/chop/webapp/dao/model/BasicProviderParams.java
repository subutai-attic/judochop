package org.apache.usergrid.chop.webapp.dao.model;


import org.apache.usergrid.chop.api.ProviderParams;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;


public class BasicProviderParams implements ProviderParams {

    private String username;
    private String instanceType;
    private String accessKey;
    private String secretKey;
    private String imageId;
    private String keyName;
    private Map<String, String> keys = new HashMap<String, String>();

    public BasicProviderParams ( String username ) {
        this(username, "", "", "", "" );
    }

    public BasicProviderParams ( String username, String instanceType, String accessKey, String secretKey, String imageId ) {

        this.username = username;
        this.instanceType = instanceType;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.imageId = imageId;
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
    public String getKeyName() {
        return keyName;
    }


    public void setKeyName(String keyName) {
        this.keyName = keyName;
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
                .append( "accessKey", accessKey )
                .append( "secretKey", secretKey )
                .append( "imageId", imageId )
                .append( "keyName", keyName )
                .append( "keys", keys )
                .toString();
    }
}
