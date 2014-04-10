package org.apache.usergrid.chop.example;


/**
 * Watch interface.
 */
public interface Watch {
    long getTime();
    boolean isDead();
    void addPowerSource( PowerSource powerSource );
    Type getType();
}
