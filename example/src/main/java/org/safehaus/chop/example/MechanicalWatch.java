package org.safehaus.chop.example;


import com.google.common.base.Preconditions;
import com.google.inject.Inject;


/**
 * A mechanical watch.
 */
public class MechanicalWatch implements Watch {
    private Mainspring spring;


    @Inject
    public void installSpring( Mainspring spring ) {
        Preconditions.checkState( spring.hasEnergy(), "Make sure the spring is wound before starting." );
        this.spring = spring;
    }


    public void wind( long amount ) {
        spring.windSpring( amount );
    }


    @Override
    public long getTime() {
        Preconditions.checkState( spring.hasEnergy(), "Can't get the time if the spring is not wound." );
        return System.currentTimeMillis();
    }


    @Override
    public boolean isDead() {
        return ! spring.hasEnergy();
    }


    @Override
    public Type getType() {
        return Type.MECHANICAL;
    }
}
