package org.safehaus.chop.stack;


import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * A stack of clusters to be tested by Judo Chop.
 */
@JsonDeserialize( as = BasicStack.class )
public interface Stack extends Serializable {

    /**
     * Gets a human legible name for this Stack.
     *
     * @return the human readable name
     */
    @JsonProperty
    String getName();

    /**
     * Gets a unique identifier for this Stack.
     *
     * @return a unique identifier as a UUID
     */
    @JsonProperty
    UUID getId();

    /**
     * Gets a list of Clusters associated with this Stack where the list order
     * reflects Cluster creation order.
     *
     * @return list of Clusters in order of creation
     */
    @JsonProperty
    List<? extends Cluster> getClusters();
}
