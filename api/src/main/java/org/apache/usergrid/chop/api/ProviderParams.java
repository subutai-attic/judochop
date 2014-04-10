package org.apache.usergrid.chop.api;


import java.util.Map;

/**
 * This contains the parameters necessary to manage all environment dependant cluster operations,
 * such as creating, launching, destroying instances
 */
public interface ProviderParams {

    /**
     * Just a plain username string for now to identify whose parameters this is
     * May be a different type later
     */
    String getUsername();

    String getInstanceType();

    String getAvailabilityZone();

    String getAccessKey();

    String getSecretKey();

    String getImageId();

    String getSecurityGroup();

    String getRunnerName();

    /**
     * Path to key files identified by key-pair-names.
     */
    Map<String, String> getKeys();

}
