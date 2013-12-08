package org.safehaus.perftest.api;


import org.junit.Test;
import org.safehaus.perftest.api.example.DummyModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.TestCase.assertNotNull;


/**
 * Testing the TestInfo.
 */
public class TestInfoTest {
    Injector injector = Guice.createInjector( new DummyModule() );


    @Test
    public void testTestInfo()
    {
        TestInfo info = injector.getInstance( TestInfo.class );
        assertNotNull( info );

        info.getCreateTimestamp();
    }
}
