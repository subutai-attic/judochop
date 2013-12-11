package org.safehaus.perftest.client;


import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;


/** Tests the PerftestClient implementations. */
@RunWith(JukitoRunner.class)
@UseModules(PerftestClientModule.class)
public class PerftestClientTest {
    private static final Logger LOG = LoggerFactory.getLogger( PerftestClientTest.class );
    @Inject
    PerftestClient client;


    @Test
    public void deleteTests() throws Exception {
        client.deleteTests();
    }


    @Test
    public void testGetTests() throws IOException {
        Set<TestInfo> tests = client.getTests();

        for ( TestInfo test : tests ) {
            LOG.debug( "Got a test: {}", test );
            LOG.debug( "Test md5: {}", test.getWarMd5() );
        }
    }


    @Test
    public void testLoad() throws IOException {
        RunnerInfo liveRunner = client.getLiveRunner();
        client.load( liveRunner, "tests/17440b961d287ead451916afaef7c2a22764423e/perftest.war", true );
    }


    @Test
    public void testGetRunners() throws Exception {
        Collection<RunnerInfo> runners = client.getRunners();

        for ( RunnerInfo info : runners ) {
            LOG.debug( "Got runner {}", info );
        }
    }


    @Test
    public void testVerify() throws Exception {
        boolean verified = client.verify();
        LOG.debug( "Verified: {}", verified );
    }


    @Test
    public void testStart() throws Exception {
        Result result = client.start( client.getLiveRunner(), true );
        LOG.debug( "Start result is {}", result.getMessage() );
    }
}
