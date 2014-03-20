package org.safehaus.chop.api.store.amazon;


import java.util.Collection;

import org.safehaus.chop.spi.Instance;
import org.safehaus.chop.spi.IpRuleManager;
import org.safehaus.chop.stack.IpRule;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 3/20/14 Time: 11:54 PM To change this template use File | Settings
 * | File Templates.
 */
public class AmazonIpRuleManager implements IpRuleManager {
    @Override
    public boolean createRuleSet( final String name ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean deleteRuleSet( final String name ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<String> listRuleSets() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean exists( final String name ) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<IpRule> getRuleSet( final String name, final boolean inbound ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void deleteRules( final String name, final IpRule... ipRules ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void deleteRules( final String name, final Collection<IpRule> ipRules ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void deleteRules( final String name, final Collection<String> ipRanges, final String protocol,
                             final int port ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void addRules( final String name, final Collection<String> ipRanges, final String protocol,
                          final int port ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void updateRules( final String name, final Collection<Instance> instances, final Collection<Integer> ports,
                             final boolean clearAllRecords ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
