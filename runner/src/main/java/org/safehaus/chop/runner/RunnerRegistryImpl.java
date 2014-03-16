package org.safehaus.chop.runner;


import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.RunnerRegistry;

import org.apache.commons.lang.NotImplementedException;

import com.google.inject.Singleton;


/**
 * An implementation of the RunnerRegistry SPI interface to hit coordinator services.
 */
@Singleton
public class RunnerRegistryImpl implements RunnerRegistry {
    private boolean started;

    @Override
    public void start() {
        started = true;
    }


    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void stop() {
        started = false;
    }


    @Override
    public Map<String, Runner> getRunners() {
        throw new NotImplementedException( "Implement later!" );
    }


    @Override
    public Map<String, Runner> getRunners( final Runner runner ) {
        throw new NotImplementedException( "Implement later!" );
    }


    @Override
    public void register( final Runner runner ) {
    }


    @Override
    public void unregister( final Runner runner ) {
        throw new NotImplementedException( "Implement later!" );
    }


    @Override
    public void deleteGhostRunners( final Set<String> activeRunners ) {
        throw new NotImplementedException( "Implement later!" );
    }
}
