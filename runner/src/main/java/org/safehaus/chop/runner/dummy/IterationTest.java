package org.safehaus.chop.runner.dummy;


import org.junit.Test;
import org.safehaus.chop.api.IterationChop;


/**
 * A dummy test file for our chops.
 */
@IterationChop( iterations = 1000, runners = 4, threads = 4 )
public class IterationTest {
    @Test
    public void testDummy() {
    }
}
