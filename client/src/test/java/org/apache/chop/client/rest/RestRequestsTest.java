package org.apache.chop.client.rest;


import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.safehaus.chop.spi.Store;
import org.usergrid.chop.client.ChopClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
@RunWith(JukitoRunner.class)
@UseModules(ChopClientModule.class)
public class RestRequestsTest {

    private static final Logger LOG = LoggerFactory.getLogger( RestRequestsTest.class );
//    @Inject
//    Store service;


    @Before
    public void setup() {
//        service.start();
    }


    @After
    public void tearDown() {
//        service.stop();
    }


    @Test @Ignore
    public void testStart() {
//        Map<String, Runner> runners = service.getRunners();
//
//        if ( runners.size() == 0 ) {
//            LOG.debug( "No drivers found, cannot start test" );
//            return;
//        }
//
//        Runner firstRunner = runners.values().iterator().next();
//        Result result = RestRequests.start( firstRunner );
//
//        if ( !result.getStatus() ) {
//            LOG.debug( "Could not get the result of start request" );
//        }
//        else {
//            LOG.debug( "Result: " + result.getMessage() );
//        }
    }


    @Test @Ignore
    public void testStatus() throws Exception {
//        Map<String, Runner> runners = service.getRunners();
//
//        for ( Runner runner : runners.values() ) {
//            if ( runner.getHostname() != null ) {
//                LOG.info( "Getting status for host: " + runner.getHostname() );
//                ChopUtils.installCert( runner.getHostname(), runner.getServerPort(), null );
//                Result result = status( runner );
//                LOG.debug( "Status result of runner {} = {}", runner.getHostname(), result );
//            }
//        }
    }
}
