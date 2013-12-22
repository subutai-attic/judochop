package org.safehaus.chop.server.dummy;


import org.junit.Test;
import org.safehaus.chop.api.annotations.IterationChop;
import org.safehaus.chop.api.annotations.TimeChop;


/**
 * A dummy test file for our chops.
 */
@TimeChop( time = 20000, runners = 4, threads = 4 )
public class TimeTest {
    @Test
    public void testDummy() throws InterruptedException {
        Thread.sleep( 1000 );
    }
}
