package org.safehaus.chop.api.store.amazon;


import java.util.Collection;

import org.safehaus.chop.spi.Instance;
import org.safehaus.chop.spi.InstanceManager;
import org.safehaus.chop.spi.InstanceState;
import org.safehaus.chop.spi.LaunchResult;
import org.safehaus.chop.stack.InstanceSpec;


/**
 *
 */
public class EC2InstanceManager implements InstanceManager {

    @Override
    public int getDefaultTimeout() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public String terminateInstances( final Collection<String> instancesIds ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public LaunchResult launchInstances( final InstanceSpec spec, final int count, final int timeout ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<Instance> getInstances() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<Instance> getInstances( final InstanceState state ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Collection<Instance> getInstances( final String instanceName ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
