package org.safehaus.chop.api.provider;


import java.util.Set;


/**
 * A security group.
 */
public interface IpRuleSet {
    String getName();

    String getId();

    Set<IpRule> getInboundRules();

    Set<IpRule> getOutboundRules();
}
