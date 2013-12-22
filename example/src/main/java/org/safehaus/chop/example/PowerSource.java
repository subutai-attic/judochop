package org.safehaus.chop.example;


/**
 * A power source for watches.
 */
public interface PowerSource {
    boolean hasPower();
    void refill( long energyTime );
}
