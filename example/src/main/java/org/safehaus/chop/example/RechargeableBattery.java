package org.safehaus.chop.example;


/**
 *  A rechargeable watch battery.
 */
public class RechargeableBattery extends Battery {
    public void recharge( long energyTime ) {
        remainingChargeTime = System.currentTimeMillis() + energyTime;
    }

    @Override
    public void refill( final long energyTime ) {
        recharge( energyTime );
    }
}
