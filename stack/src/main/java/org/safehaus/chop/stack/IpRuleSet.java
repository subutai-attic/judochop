package org.safehaus.chop.stack;


import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * A security group.
 */
public interface IpRuleSet {
    @JsonProperty
    String getName();

    @JsonProperty
    String getId();

    @JsonProperty
    Set<IpRule> getInboundRules();

    @JsonProperty
    Set<IpRule> getOutboundRules();
}
