package org.safehaus.chop.client;


import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static junit.framework.TestCase.assertEquals;


/** Tests the ChopClient implementations. */
@RunWith(JukitoRunner.class)
@UseModules(ChopClientModule.class)
public class ChopClientTest {
    private static final Logger LOG = LoggerFactory.getLogger( ChopClientTest.class );
    @Inject
    ChopClient client;


    @Test @Ignore
    public void deleteTests() throws Exception {
        client.deleteTests();
    }


    @Test
    public void testGetTests() throws IOException {
        Set<Project> tests = client.getProjectConfigs();

        for ( Project test : tests ) {
            LOG.debug( "Got a test: {}", test );
            LOG.debug( "Test md5: {}", test.getWarMd5() );
        }
    }


    @Test @Ignore( "make sure this is not hard coded and does not break" )
    public void testLoad() throws IOException {
        RunnerFig liveRunnerFig = client.getLiveRunner();
        client.load( liveRunnerFig, "tests/17440b961d287ead451916afaef7c2a22764423e/perftest.war", null );
    }


    @Test
    public void testGetRunners() throws Exception {
        Collection<RunnerFig> runnerFigs = client.getRunners();

        for ( RunnerFig info : runnerFigs ) {
            LOG.debug( "Got runnerFig {}", info );
        }
    }


    @Test
    public void testVerify() throws Exception {
        Result result = client.verify();
        LOG.debug( "Verification result: {} , with state: {}", result.getMessage(), result.getState() );
    }


    @Test @Ignore
    public void testStart() throws Exception {
        Result result = client.start( client.getLiveRunner() );
        LOG.debug( "Start result is {}", result.getMessage() );
    }


    @Test
    public void testCompareTimeStamps1() throws NumberFormatException {
        String date1 = "2013.12.12.23.00.02";
        String date2 = "2013.12.12.23.00.03";

        assertEquals( "Compare Timestamp is faulty", -1, client.compareTimestamps( date1, date2 ) );
    }


    @Test
    public void testCompareTimeStamps2() throws NumberFormatException {
        String date1 = "2013.12.12.23.00.02";
        String date2 = "2013.12.12.23.52.02";

        assertEquals( "Compare Timestamp is faulty", -1, client.compareTimestamps( date1, date2 ) );
    }


    @Test
    public void testCompareTimeStamps3() throws NumberFormatException {
        String date1 = "2013.12.12.00.00.02";
        String date2 = "2013.12.12.23.00.02";

        assertEquals( "Compare Timestamp is faulty", 1, client.compareTimestamps( date2, date1 ) );
    }


    @Test
    public void testCompareTimeStamps4() throws NumberFormatException {
        String date1 = "2013.12.12.07.00.02";
        String date2 = "2013.12.12.15.00.02";

        assertEquals( "Compare Timestamp is faulty", 1, client.compareTimestamps( date2, date1 ) );
    }


    @Test
    public void testCompareTimeStamps5() throws NumberFormatException {
        String date1 = "2013.12.12.07.00.02";
        String date2 = "2013.12.12.11.00.02";

        assertEquals( "Compare Timestamp is faulty", -1, client.compareTimestamps( date1, date2 ) );
    }


    @Test
    public void testCompareTimeStamps6() throws NumberFormatException {
        String date1 = "2013.02.12.23.00.02";
        String date2 = "2013.12.12.23.52.02";

        assertEquals( "Compare Timestamp is faulty", -1, client.compareTimestamps( date1, date2 ) );
    }
}
