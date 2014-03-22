package org.safehaus.chop.api.store.amazon;


import java.util.Collection;
import java.util.Set;

import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.spi.InstanceRegistry;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 3/21/14 Time: 12:05 AM To change this template use File | Settings
 * | File Templates.
 */
public class EC2InstanceRegistry implements InstanceRegistry {
    @Override
    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean isStarted() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<Instance> getInstances() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void register( final Instance instance ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void unregister( final Instance instance ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void deleteGhostInstances( final Set<String> exclusions ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
