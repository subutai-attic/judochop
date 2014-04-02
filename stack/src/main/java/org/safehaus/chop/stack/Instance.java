package org.safehaus.chop.stack;


/**
 * A virtual machine or lxc instance.
 */
public interface Instance {

    String getId();

    InstanceSpec getSpec();

    InstanceState getState();

    String getPrivateDnsName();

    String getPublicDnsName();

    String getPrivateIpAddress();

    String getPublicIpAddress();
}
