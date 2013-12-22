package org.safehaus.chop.example;


/**
 *  A watch battery.
 */
public class Battery {
    private final long deathTime = System.currentTimeMillis() + 3000L;

    boolean isDead() {
        return deathTime - System.currentTimeMillis() <= 0;
    }
}
