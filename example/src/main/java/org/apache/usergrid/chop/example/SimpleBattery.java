package org.apache.usergrid.chop.example;


/**
 *  A simple watch battery.
 */
public class SimpleBattery extends Battery {
    @Override
    public void refill( final long energyTime ) {
        throw new UnsupportedOperationException( "This battery is not rechargeable." );
    }
}
