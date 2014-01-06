package org.safehaus.chop.client.rest;


import java.util.Map;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Result;
import org.safehaus.chop.api.RunnerFig;
import org.safehaus.chop.api.StoreService;
import org.safehaus.chop.client.PerftestClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static org.safehaus.chop.client.rest.RestRequests.status;


/**
 *
 */
@RunWith(JukitoRunner.class)
@UseModules(PerftestClientModule.class)
public class RestRequestsTest {
    private static final Logger LOG = LoggerFactory.getLogger( RestRequestsTest.class );
    @Inject
    StoreService service;


    @Before
    public void setup() {
        service.start();
    }


    @After
    public void tearDown() {
        service.stop();
    }


    @Test @Ignore
    public void testStart() {
        Map<String, RunnerFig> runners = service.getRunners();

        if ( runners.size() == 0 ) {
            LOG.debug( "No drivers found, cannot start test" );
            return;
        }

        RunnerFig firstRunnerFig = runners.values().iterator().next();
        Result result = RestRequests.start( firstRunnerFig, true );

        if ( !result.getStatus() ) {
            LOG.debug( "Could not get the result of start request" );
        }
        else {
            LOG.debug( "Result: " + result.getMessage() );
        }
    }


    @Test
    public void testStatus() {
        Map<String, RunnerFig> runners = service.getRunners();

        for ( RunnerFig runnerFig : runners.values() ) {
            if ( runnerFig.getHostname() != null ) {
                Result result = status( runnerFig );
                LOG.debug( "Status result of runnerFig {} = {}", runnerFig.getHostname(), result );
            }
        }
    }
}
