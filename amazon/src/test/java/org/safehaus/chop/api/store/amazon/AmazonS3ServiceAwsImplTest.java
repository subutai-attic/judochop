package org.safehaus.chop.api.store.amazon;


import java.util.Map;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.api.store.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static junit.framework.TestCase.assertNotNull;


/** Tests the Amazon based PerftestStore implementation. */
@RunWith( JukitoRunner.class )
@UseModules( AmazonStoreModule.class )
public class AmazonS3ServiceAwsImplTest {
    private static final Logger LOG = LoggerFactory.getLogger( AmazonS3ServiceAwsImplTest.class );
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


    @Test
    public void testGetRunners() {
        Map<String, Runner> runners = service.getRunners();
        assertNotNull( runners );
        int runnerCount = 0;

        for ( Runner runner : runners.values() ) {
            runnerCount++;
            LOG.debug( "Got runner {}", runner );
        }

        if ( runnerCount == 0 ) {
            LOG.warn( "Not much of a test if we got no drivers" );
        }
    }
}
