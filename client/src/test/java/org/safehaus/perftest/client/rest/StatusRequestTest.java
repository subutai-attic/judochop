package org.safehaus.perftest.client.rest;


import java.util.Collection;
import java.util.Map;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.store.StoreService;
import org.safehaus.perftest.client.PerftestClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 *
 */
@RunWith( JukitoRunner.class )
@UseModules( PerftestClientModule.class )
public class StatusRequestTest {
    private static final Logger LOG = LoggerFactory.getLogger( StatusRequestTest.class );
    StoreService service;


    @Before
    public void setup() {
        Injector injector = Guice.createInjector( new PerftestClientModule() );
        service = injector.getInstance( StoreService.class );
        service.start();
    }


    @After
    public void tearDown() {
        service.stop();
    }


    @Test
    public void testStatus() throws Exception {
        StatusRequest request;
        Map<String,RunnerInfo> runners = service.getRunners();

        for ( RunnerInfo runner : runners.values() ) {
            request = new StatusRequest();
            Result result = request.status( runner );

            if ( runner.getHostname() != null )
            {
                LOG.debug( "Status result of runner {} = {}", runner.getHostname(), result );
            }
        }
    }
}
