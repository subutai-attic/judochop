package org.apache.usergrid.chop.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A mainspring used to power mechanical watches.
 */
public class Mainspring implements PowerSource {
    private static final Logger LOG = LoggerFactory.getLogger( Mainspring.class );
    private long energyLeftUntil = System.currentTimeMillis() + 1300L;


    public Mainspring() {
        LOG.debug( "Spring created with {} milliseconds of energy.", energyLeftUntil - System.currentTimeMillis() );
    }


    public void windSpring( long energyTime ) {
        energyLeftUntil = System.currentTimeMillis() + energyTime;
    }


    @Override
    public boolean hasPower() {
        return ( energyLeftUntil - System.currentTimeMillis() ) >= 0;
    }


    @Override
    public void refill( final long energyTime ) {
        windSpring( energyTime );
    }
}
