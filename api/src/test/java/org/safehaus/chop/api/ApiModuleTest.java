package org.safehaus.chop.api;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.TestCase.assertNotNull;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/22/13 Time: 1:33 AM To change this template use File | Settings
 * | File Templates.
 */
public class ApiModuleTest {
    private static final Logger LOG = LoggerFactory.getLogger( ApiModuleTest.class );


    @Test
    public void testProject() {
        Injector injector = Guice.createInjector( new ApiModule() );

        Project project = injector.getInstance( Project.class );
        assertNotNull( project );
        LOG.debug( "project =\n{}", project );
    }
}
