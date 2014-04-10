package org.apache.usergrid.chop.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  A watch battery base class.
 */
public abstract class Battery implements PowerSource {
    private static final Logger LOG = LoggerFactory.getLogger( Mainspring.class );
    protected long remainingChargeTime = System.currentTimeMillis() + 1300L;


    public Battery() {
        LOG.debug( "{} created with {} milliseconds of remaining power.",
                getClass().getSimpleName(), getRemainingChargeTime() );
    }


    public long getRemainingChargeTime() {
        long time = remainingChargeTime - System.currentTimeMillis();

        if ( time < 0 ) {
            return 0;
        }
        else {
            return time;
        }
    }


    @Override
    public boolean hasPower() {
        return remainingChargeTime - System.currentTimeMillis() >= 0;
    }
}
