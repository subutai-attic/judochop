package org.safehaus.perftest.client;


import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;


/**
 * Tests the PerftestClient implementations.
 */
@RunWith( JukitoRunner.class )
@UseModules( PerftestClientModule.class )
public class PerftestClientTest {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientTest.class );
    @Inject PerftestClient client;


    @Test
    public void deleteTests() throws Exception {
        client.deleteTests();
    }


    @Test
    public void testGetTests() throws IOException {
        Set<TestInfo> tests = client.getTests();

        for ( TestInfo test : tests ) {
            LOG.debug( "Got a test: {}", test );
        }
    }


//    @Test
//    public void testLoad() throws IOException {
//        RunnerInfo liveRunner = client.getLiveRunner();
//        client.load( liveRunner, "tests/bace8793f7583d2320dc8ae2025b606fed065417/perftest.war", true );
//    }


    @Test
    public void testGetRunners() throws Exception {
        Collection<RunnerInfo> runners = client.getRunners();

        for ( RunnerInfo info : runners ) {
            LOG.debug( "Got runner {}", info );
        }
    }
}
