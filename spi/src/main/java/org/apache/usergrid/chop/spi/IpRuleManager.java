package org.apache.usergrid.chop.spi;


import java.util.Collection;

import org.apache.usergrid.chop.stack.IpRule;
import org.apache.usergrid.chop.stack.IpRuleSet;


/**
 * A security group manager.
 */
public interface IpRuleManager {

    boolean createRuleSet( String name );

    boolean deleteRuleSet( String name );

    Collection<String> listRuleSets();

    boolean exists( String name );

    Collection<IpRule> getRules( String name, boolean inbound );

    IpRuleSet getIpRuleSet( String name );

    void deleteRules( String name, IpRule... ipRules );

    void deleteRules( String name, Collection<IpRule> ipRules );

    void deleteRules( String name, Collection<String> ipRanges, String protocol, int port );

    void addRules( String name, Collection<String> ipRanges, String protocol, int port );

    void addRules( String name, Collection<String> ipRanges, String protocol, int fromPort, int toPort );

    void applyIpRuleSet( IpRuleSet ruleSet );

    void setDataCenter( String dataCenter );
}
