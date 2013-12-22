package org.safehaus.chop.example;


import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.annotations.IterationChop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;


/**
 * Jukito (iteration) chopped digital watch test with member injection.
 */
@RunWith( JukitoRunner.class )
@UseModules( DigitalWatchModule.class )
@IterationChop( iterations = 10, threads = 4, runners = 4 )
public class DigitalWatchTest {
    private static final Logger LOG = LoggerFactory.getLogger( DigitalWatchTest.class );


    @Test
    public void testCreation( Watch watch ) {
        assertNotNull( watch );
        assertFalse( watch.isDead() );
        assertEquals( Type.DIGITAL, watch.getType() );
    }


    @Test
    public void testBattery( Watch watch ) throws InterruptedException {
        assertFalse( watch.isDead() );
        while ( ! watch.isDead() ) {
            Thread.sleep( 1000L );
        }
        assertTrue( watch.isDead() );

        try {
            watch.getTime();
        }
        catch ( IllegalStateException e ) {
            LOG.debug( "Watch is dead, can't read the time." );
        }

        watch.addPowerSource( new RechargeableBattery() );
        assertFalse( watch.isDead() );
        watch.getTime();
    }
}