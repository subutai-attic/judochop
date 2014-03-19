package org.safehaus.chop.spi;


import java.util.Collection;

import org.safehaus.chop.stack.IpRule;


/**
 * A security group manager.
 */
public interface IpRuleManager {

    boolean createRuleSet( String name );

    boolean deleteRuleSet( String name );

    Collection<String> listRuleSets();

    boolean exists( String name );

    Collection<IpRule> getRuleSet( String name, boolean inbound );

    void deleteRules( String name, IpRule... ipRules );

    void deleteRules( String name, Collection<IpRule> ipRules );

    void deleteRules( String name, Collection<String> ipRanges, String protocol, int port );

    void addRules( String name, Collection<String> ipRanges, String protocol, int port );

    void updateRules( String name, Collection<Instance> instances, Collection<Integer> ports, boolean clearAllRecords );

}
