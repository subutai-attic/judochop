package org.apache.usergrid.chop.stack;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Represents a group of instances working together.
 */
@JsonDeserialize( as = BasicCluster.class )
public interface Cluster {

    /**
     * The name of the cluster.
     *
     * @return the name of the cluster
     */
    @JsonProperty
    String getName();

    /**
     * The instance specification to use for creating cluster instances.
     *
     * @return the instance specification for cluster instances
     */
    @JsonProperty
    InstanceSpec getInstanceSpec();

    /**
     * The number of instances to use for the cluster.
     *
     * @return the number of cluster instances
     */
    @JsonProperty
    int getSize();
}
