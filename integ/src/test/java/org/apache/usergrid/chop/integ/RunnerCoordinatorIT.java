package org.apache.usergrid.chop.integ;


import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.safehaus.jettyjam.utils.JettyIntegResource;
import org.safehaus.jettyjam.utils.JettyResource;
import org.safehaus.jettyjam.utils.StartResources;
import org.safehaus.jettyjam.utils.TestMode;

import static junit.framework.TestCase.assertTrue;


/**
 * Integration tests the various interactions between runners and the coordinator.
 *
 * This integration test starts up the chop web UI as a jetty jam integ resource
 * and then proceeds to start up two runners generated from the example project
 * using chop:runner.
 */
public class RunnerCoordinatorIT {
    private static final String[] webappArgs = new String[] { "-e" };

    private final static Properties systemProperties = new Properties();

    static {
        systemProperties.setProperty( TestMode.TEST_MODE_PROPERTY, TestMode.UNIT.toString() );
        systemProperties.setProperty( "archaius.deployment.environment", "INTEG" );
    }

    private static JettyResource webapp = new JettyIntegResource( "jettyjam-webapp.properties", webappArgs );
    private static JettyResource runner1 = new JettyIntegResource( systemProperties );
    private static JettyResource runner2 = new JettyIntegResource( systemProperties );

    @ClassRule
    public static StartResources resources = new StartResources( 1000, webapp, runner1, runner2 );

    @Test
    public void testBasic() {
        assertTrue( webapp.isStarted() );
        assertTrue( runner1.isStarted() );
        assertTrue( runner2.isStarted() );
    }
}
