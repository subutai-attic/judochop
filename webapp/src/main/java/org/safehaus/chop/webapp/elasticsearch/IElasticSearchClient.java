package org.safehaus.chop.webapp.elasticsearch;


import org.elasticsearch.client.Client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public interface IElasticSearchClient {

    @JsonIgnore
    Client getClient();

    @JsonProperty
    String getHost();

    @JsonProperty
    int getPort();

    @JsonProperty
    String getClusterName();
}
