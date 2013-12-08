package org.safehaus.perftest.client;


import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.Assert.assertNotNull;


/**
 * Tests the PerftestClient implementations.
 */
public class PerftestClientTest {

    @Test
    public void testClient() throws Exception {
        Injector injector = Guice.createInjector( new PertestClientModule() );
        PerftestClient client = injector.getInstance( PerftestClient.class );

        assertNotNull( client );
    }

}
