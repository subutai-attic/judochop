package org.safehaus.chop.stack;


import java.net.URL;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * An IaaS provider independent instance specification encapsulates all the information
 * needed to create, configure, and enable access to an instance.
 */
public interface InstanceSpec {
    /**
     * Gets the IaaS identifier for a base image (template) used for creating instances.
     *
     * @return the image identifier specific to IaaS provider
     */
    @JsonProperty
    String getImageId();

    /**
     * Gets the IaaS identifier for the instance type. This is very provider specific
     * hence why we use just a base Enum type here.
     *
     * @return the instance type
     */
    @JsonProperty
    Enum getType();

    /**
     * Private key pair name used to authenticate into instances.
     *
     * @return the private key pair name
     */
    @JsonProperty
    String getKeyName();

    /**
     * The IP access rules for inbound and outbound traffic: in AWS this corresponds
     * to a security group.
     *
     * @return the inbound and outbound IP traffic rules
     */
    @JsonProperty
    IpRuleSet getIpRules();

    /**
     * Gets the data center where the instances will be created. In AWS this
     * corresponds to a region and an availability zone combination.
     *
     * @return the data center where instances are created
     */
    @JsonProperty
    String getDataCenter();

    /**
     * A list of scripts executed on newly created instances of this instance specification.
     *
     * @return the setup scripts
     */
    @JsonProperty
    List<URL> getSetupScripts();

    /**
     * The environment properties to inject into the shell before executing setup scripts.
     */
    @JsonProperty
    Properties getScriptEnvironment();
}
