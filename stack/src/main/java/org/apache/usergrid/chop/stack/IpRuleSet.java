package org.apache.usergrid.chop.stack;


import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * A security group.
 */
@JsonDeserialize( as = BasicIpRuleSet.class )
public interface IpRuleSet {
    @JsonProperty
    String getName();

    @JsonProperty
    UUID getId();

    @JsonProperty
    Set<IpRule> getInboundRules();

    @JsonProperty
    Set<IpRule> getOutboundRules();
}
