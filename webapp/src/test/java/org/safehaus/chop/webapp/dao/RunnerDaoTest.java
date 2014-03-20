package org.safehaus.chop.webapp.dao;

import org.junit.Test;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class RunnerDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( RunnerDaoTest.class );


    @Test
    public void delete() {
        LOG.info( "\n===RunnerDaoTest.delete===\n" );

        LOG.info( "Runners before delete: " );

        List<Runner> runners = ESSuiteTest.runnerDao.getRunners( ESSuiteTest.COMMIT_ID_2 );

        for( Runner runner: runners ) {
            LOG.info( runner.toString() );
        }

        ESSuiteTest.runnerDao.delete( ESSuiteTest.RUNNER_HOSTNAME_3 );

        runners = ESSuiteTest.runnerDao.getRunners( ESSuiteTest.COMMIT_ID_2 );

        assertEquals( 0, runners.size() );
    }

    @Test
    public void getRunners() {

        LOG.info( "\n===RunnerDaoTest.getRunners===\n" );

        List<Runner> runners = ESSuiteTest.runnerDao.getRunners( ESSuiteTest.COMMIT_ID_1 );

        for( Runner runner: runners ) {
            LOG.info( runner.toString() );
        }

        assertEquals( 2, runners.size() );
    }

}
