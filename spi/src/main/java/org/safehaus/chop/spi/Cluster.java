package org.safehaus.chop.spi;


/**
 * Represents a group of instances working together.
 */
public interface Cluster {
    String getName();

    InstanceSpec getInstanceSpec();

    int size();
}
