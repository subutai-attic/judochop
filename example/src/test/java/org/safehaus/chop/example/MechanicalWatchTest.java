package org.safehaus.chop.example;


import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.annotations.TimeChop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


/**
 * A Jukito (time) chopped mechanical watch test demonstrating Jukito member level injections.
 */
@RunWith( JukitoRunner.class )
@UseModules( MechanicalWatchModule.class )
@TimeChop( time = 30000L, threads = 4, runners = 4 )
public class MechanicalWatchTest {
    private static final Logger LOG = LoggerFactory.getLogger( MechanicalWatchTest.class );

    @Inject
    Watch watch;


    @Test
    public void testCreation() {
        LOG.debug( "Created a watch: {}", watch );
        assertNotNull( watch );
        assertFalse( watch.isDead() );
        assertEquals( Type.MECHANICAL, watch.getType() );
    }


    @Test
    public void testWatch() throws InterruptedException {
        assertFalse( watch.isDead() );
        while ( ! watch.isDead() ) {
            Thread.sleep( 300L );
        }
        assertTrue( watch.isDead() );

        try {
            watch.getTime();
            fail( "A dead watch cannot tell time." );
        }
        catch ( IllegalStateException e ) {
            LOG.debug( "Watch is dead, can't read the time." );
        }

        ( ( MechanicalWatch ) watch ).wind( 1000L );
        assertFalse( watch.isDead() );
        watch.getTime();
    }
}
