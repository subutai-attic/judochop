package org.apache.usergrid.chop.webapp.dao;

import org.junit.Test;
<<<<<<< HEAD:webapp/src/test/java/org/safehaus/chop/webapp/dao/RunResultDaoTest.java
import org.apache.usergrid.chop.api.Run;
import org.apache.usergrid.chop.api.RunResult;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
=======
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.apache.usergrid.chop.webapp.elasticsearch.ESSuiteTest;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/test/java/org/apache/usergrid/chop/webapp/dao/RunResultDaoTest.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class RunResultDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( RunResultDaoTest.class );


    @Test
    public void getAll() {

        LOG.info( "\n\n===RunResultDaoTest.getAll===" );

        List<RunResult> list = ESSuiteTest.runResultDao.getAll();

        for ( RunResult runResult : list ) {
            LOG.info( runResult.toString() );
        }

        assertEquals( 3, list.size() );
    }


    @Test
    public void getMap() {

        LOG.info( "\n\n===RunResultDaoTest.getMap===" );

        Map<String, Run> runs = ESSuiteTest.runDao.getMap( ESSuiteTest.COMMIT_ID_2, 2, ESSuiteTest.TEST_NAME );
        Map<Run, List<RunResult>> runResults = ESSuiteTest.runResultDao.getMap( runs );

        for ( Run run : runResults.keySet() ) {
            LOG.info( run.toString() );

            for (RunResult runResult : runResults.get( run ) ) {
                LOG.info( "   {}", runResult.toString() );
            }
        }

        assertEquals( 1, runResults.size() );
    }


    @Test
    public void deleteAll() {

        LOG.info( "\n\n=== RunResultDaoTest.deleteAll() ===" );

        for ( RunResult runResult : ESSuiteTest.runResultDao.getAll() ) {
            ESSuiteTest.runResultDao.delete( runResult.getId() );
        }

        List<RunResult> list = ESSuiteTest.runResultDao.getAll();

        assertEquals( 0, list.size() );
    }

}
