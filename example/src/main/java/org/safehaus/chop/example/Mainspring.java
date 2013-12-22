package org.safehaus.chop.example;


/**
 * A mainspring in a watch.
 */
public class Mainspring {
    private long energyLeftUntil = System.currentTimeMillis() + 5000L;

    public void windSpring( long time ) {
        energyLeftUntil += time;
    }


    public boolean hasEnergy() {
        return ( energyLeftUntil - System.currentTimeMillis() ) <= 0;
    }
}
