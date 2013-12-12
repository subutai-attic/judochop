package org.safehaus.perftest.client.rest;


import java.util.Map;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.store.StoreService;
import org.safehaus.perftest.client.PerftestClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static org.safehaus.perftest.client.rest.RestRequests.status;


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
        Map<String, RunnerInfo> runners = service.getRunners();

        if ( runners.size() == 0 ) {
            LOG.debug( "No runners found, cannot start test" );
            return;
        }

        RunnerInfo firstRunner = runners.values().iterator().next();
        Result result = RestRequests.start( firstRunner, true );

        if ( !result.getStatus() ) {
            LOG.debug( "Could not get the result of start request" );
        }
        else {
            LOG.debug( "Result: " + result.getMessage() );
        }
    }


    @Test
    public void testStatus() {
        Map<String, RunnerInfo> runners = service.getRunners();

        for ( RunnerInfo runner : runners.values() ) {
            if ( runner.getHostname() != null ) {
                Result result = status( runner );
                LOG.debug( "Status result of runner {} = {}", runner.getHostname(), result );
            }
        }
    }
}
