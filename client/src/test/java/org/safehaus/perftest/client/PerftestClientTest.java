package org.safehaus.perftest.client;


import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.safehaus.perftest.api.RunnerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.Assert.assertNotNull;


/**
 * Tests the PerftestClient implementations.
 */
public class PerftestClientTest {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientTest.class );
    private PerftestClient client;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        client = injector.getInstance( PerftestClient.class );
    }


    @Test
    public void testClientGuice() throws Exception {
        assertNotNull( client );
    }


    @Test
    public void testGetRunners() throws Exception {
        Collection<RunnerInfo> runners = client.getRunners();

        for ( RunnerInfo info : runners )
        {
            LOG.debug( "Got runner {}", info );
        }
    }
}
