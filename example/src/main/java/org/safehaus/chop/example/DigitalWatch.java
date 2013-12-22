package org.safehaus.chop.example;


import com.google.common.base.Preconditions;
import com.google.inject.Inject;


/**
 * A digital watch that runs on batteries.
 */
public class DigitalWatch implements Watch {
    private Battery battery;


    @Inject
    public void addPowerSource( PowerSource powerSource ) {
        Preconditions.checkState( powerSource.hasPower(), "Don't install a dead battery" );
        Preconditions.checkState( powerSource instanceof Battery );

        //noinspection ConstantConditions
        this.battery = ( Battery ) powerSource;
    }


    @Override
    public long getTime() {
        Preconditions.checkState( battery.hasPower(), "Can't tell time with a dead battery!" );
        return System.currentTimeMillis();
    }


    @Override
    public boolean isDead() {
        return ! battery.hasPower();
    }


    @Override
    public Type getType() {
        return Type.DIGITAL;
    }
}
