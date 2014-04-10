package org.apache.usergrid.chop.runner;


import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Test;

import org.safehaus.jettyjam.utils.JettyIntegResource;
import org.safehaus.jettyjam.utils.JettyResource;

import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An integration test for the chop UI.
 */
public class RunnerAppIT {
    private final static Logger LOG = LoggerFactory.getLogger( RunnerAppIT.class );
    private final static Properties systemProperties = new Properties();

    static {
        systemProperties.setProperty( TestMode.TEST_MODE_PROPERTY, TestMode.INTEG.toString() );
    }

    @ClassRule
    public static JettyResource jetty = new JettyIntegResource( systemProperties );


    @Test
    public void testStart() {
        RunnerTestUtils.testStart( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testReset() {
        RunnerTestUtils.testReset( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testStop() {
        RunnerTestUtils.testStop( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testStats() {
        RunnerTestUtils.testStats( jetty.newTestParams().setLogger( LOG ) );
    }


    @Test
    public void testStatus() {
        RunnerTestUtils.testStatus( jetty.newTestParams().setLogger( LOG ) );
    }
}
