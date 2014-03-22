package org.safehaus.chop.stack;


import java.net.URL;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * An IaaS provider independent instance specification encapsulates all the information
 * needed to create, configure, and enable access to an instance.
 */
@JsonDeserialize( as = BasicInstanceSpec.class )
public interface InstanceSpec {
    /**
     * Gets the IaaS identifier for a base image (template) used for creating instances.
     *
     * @return the image identifier specific to IaaS provider
     */
    @JsonProperty
    String getImageId();

    /**
     * Gets the IaaS identifier for the instance type. This is very provider specific.
     *
     * @return the instance type
     */
    @JsonProperty
    String getType();

    /**
     * Private key pair name used to authenticate into instances.
     *
     * @return the private key pair name
     */
    @JsonProperty
    String getKeyName();

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
