package org.safehaus.chop.example;


import com.google.common.base.Preconditions;
import com.google.inject.Inject;


/**
 * A mechanical watch powered by a Mainspring.
 */
public class MechanicalWatch implements Watch {
    private Mainspring spring;


    @Inject
    public void addPowerSource( PowerSource powerSource ) {
        Preconditions.checkState( powerSource.hasPower(), "Make sure the spring is wound before starting." );
        this.spring = ( Mainspring ) powerSource;
    }


    public void wind( long amount ) {
        spring.windSpring( amount );
    }


    @Override
    public long getTime() {
        Preconditions.checkState( spring.hasPower(), "Can't get the time if the spring is not wound." );
        return System.currentTimeMillis();
    }


    @Override
    public boolean isDead() {
        return ! spring.hasPower();
    }


    @Override
    public Type getType() {
        return Type.MECHANICAL;
    }
}
