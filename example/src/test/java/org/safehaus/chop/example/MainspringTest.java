package org.safehaus.chop.example;


import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * Vanilla JUnit unit test without annotations.
 */
public class MainspringTest {
    @Test
    public void testMainSpring() throws InterruptedException {
        Mainspring mainspring = new Mainspring();
        assertTrue( mainspring.hasPower() );
        while ( mainspring.hasPower() ) {
            Thread.sleep( 300L );
        }

        assertFalse( mainspring.hasPower() );
        mainspring.windSpring( 3000L );
        assertTrue( mainspring.hasPower() );
    }
}
