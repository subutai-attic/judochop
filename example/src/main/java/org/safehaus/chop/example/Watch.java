package org.safehaus.chop.example;


/**
 * Watch interface.
 */
public interface Watch {
    long getTime();
    boolean isDead();
    void addPowerSource( PowerSource powerSource );
    Type getType();
}
