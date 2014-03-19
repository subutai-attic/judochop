package org.safehaus.chop.stack;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * A basic IpRuleSet implementation.
 */
public class BasicIpRuleSet implements IpRuleSet {
    private String name;
    private UUID id = UUID.randomUUID();
    private Set<IpRule> inboundRules = new HashSet<IpRule>();
    private Set<IpRule> outboundRules = new HashSet<IpRule>();


    @Override
    public String getName() {
        return name;
    }


    public void setName( String name ) {
        this.name = name;
    }


    @Override
    public UUID getId() {
        return id;
    }


    public void setId( UUID id ) {
        this.id = id;
    }


    @Override
    public Set<IpRule> getInboundRules() {
        return inboundRules;
    }


    public BasicIpRuleSet addInboundRule( IpRule rule ) {
        this.inboundRules.add( rule );
        return this;
    }


    @Override
    public Set<IpRule> getOutboundRules() {
        return outboundRules;
    }


    public BasicIpRuleSet addOutboundRule( IpRule rule ) {
        this.outboundRules.add( rule );
        return this;
    }
}
