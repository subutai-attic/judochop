package org.safehaus.perftest.client.rest;


import java.io.IOException;
import java.util.Map;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.perftest.api.Result;
import org.safehaus.perftest.api.RunnerInfo;
import org.safehaus.perftest.api.store.StoreOperations;
import org.safehaus.perftest.client.PerftestClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/9/13 Time: 1:04 AM To change this template use File | Settings |
 * File Templates.
 */
@RunWith( JukitoRunner.class )
@UseModules( PerftestClientModule.class )
public class LoadRequestTest {
    private static final Logger LOG = LoggerFactory.getLogger( LoadRequestTest.class );

    @Inject
    StoreOperations operations;

    @Test
    public void testLoad() throws IOException {
        Map<String,RunnerInfo> runners = operations.getRunners();
        Result result;

        for ( RunnerInfo runner : runners.values() ) {
            if ( runner.getHostname() == null ) {
                LOG.error( "Skipping runner with null hostname: {}" + runner );
            }
            else {
                LoadRequest request = new LoadRequest();

                if ( runner.getUrl() == null ) {
                    LOG.error( "Skipping runner with null URL: {}", runner );
                    continue;
                }

//                request.load( runner,  );
            }
        }
    }
}
