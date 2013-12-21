package org.safehaus.chop.dummy;


import org.junit.Test;
import org.safehaus.chop.api.annotations.IterationChop;


/**
 * A dummy test file for our chops.
 */
@IterationChop( iterations = 1000, runners = 4, threads = 4 )
public class DummyTest {
    @Test
    public void testDummy() {
    }
}
