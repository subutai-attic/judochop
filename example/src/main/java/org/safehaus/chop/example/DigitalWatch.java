package org.safehaus.chop.example;


import com.google.common.base.Preconditions;
import com.google.inject.Inject;


/**
 * A digital watch that runs on batteries.
 */
public class DigitalWatch implements Watch {
    private Battery battery;


    @Inject
    public void installBattery( Battery battery ) {
        Preconditions.checkState( ! battery.isDead(), "Don't install a dead battery" );
        this.battery = battery;
    }


    @Override
    public long getTime() {
        Preconditions.checkState( ! battery.isDead(), "Can't tell time with a dead battery!" );
        return System.currentTimeMillis();
    }


    @Override
    public boolean isDead() {
        return battery.isDead();
    }


    @Override
    public Type getType() {
        return Type.DIGITAL;
    }
}
