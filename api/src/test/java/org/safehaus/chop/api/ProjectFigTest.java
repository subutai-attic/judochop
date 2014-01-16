package org.safehaus.chop.api;


import org.junit.Test;
import org.safehaus.guicyfig.GuicyFigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.TestCase.assertNotNull;


/**
 * Tests basic project creation.
 */
public class ProjectFigTest {
    private static final Logger LOG = LoggerFactory.getLogger( ProjectFigTest.class );


    @Test
    public void testProject() {
        Injector injector = Guice.createInjector( new GuicyFigModule( ProjectFig.class ) );
        ProjectFig project = injector.getInstance( ProjectFig.class );
        assertNotNull( project );
        LOG.debug( "project =\n{}", project );
    }
}