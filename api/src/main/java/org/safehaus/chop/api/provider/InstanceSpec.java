package org.safehaus.chop.api.provider;


import java.net.URL;
import java.util.List;


/**
 * An instance specification.
 */
public interface InstanceSpec {
    String getImageId();

    Enum getType();

    String getKeyName();

    IpRuleSet getSecurityGroup();

    String getAvailabilityZone();

    List<URL> getPostSetupScripts();
}
