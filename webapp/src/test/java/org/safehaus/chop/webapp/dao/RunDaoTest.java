package org.safehaus.chop.webapp.dao;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class RunDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( RunDaoTest.class );


    @Test
    public void getAll() {

        LOG.info( "\n===RunDaoTest.getAll===\n" );

        List<Run> list = ESSuiteTest.runDao.getAll();

        for ( Run run : list ) {
            LOG.info( run.toString() );
        }

        assertEquals( 3, list.size() );
    }


    @Test
    public void getListByCommits() {

        LOG.info( "\n===RunDaoTest.getListByCommits===\n" );

        List<Commit> commits = ESSuiteTest.commitDao.getByModule( ESSuiteTest.MODULE_ID_2 );

        List<Run> list = ESSuiteTest.runDao.getList( commits, ESSuiteTest.TEST_NAME );

        for ( Run run : list ) {
            LOG.info( run.toString() );
        }

        // @todo assertEquals number of runs
    }


    @Test
    public void getListByCommit() {

        LOG.info( "\n===RunDaoTest.getListByCommit===\n" );

        List<Run> list = ESSuiteTest.runDao.getList( ESSuiteTest.COMMIT_ID_2 , ESSuiteTest.TEST_NAME );

        for ( Run run : list ) {
            LOG.info( run.toString() );
        }

        assertEquals( 2, list.size() );
    }


    @Test
    public void getListByCommitAndRunNumber() {

        LOG.info( "\n===RunDaoTest.getListByCommitAndRunNumber===\n" );

        int runNumber = 2;

        List<Run> list = ESSuiteTest.runDao.getList( ESSuiteTest.COMMIT_ID_2, runNumber );

        for (Run run : list) {
            LOG.info( run.toString() );
        }

        assertEquals( 1, list.size() );
    }


    @Test
    public void getNextRunNumber() {

        LOG.info( "\n===RunDaoTest.getNextRunNumber===\n" );

        int nextRunNumber = ESSuiteTest.runDao.getNextRunNumber( ESSuiteTest.COMMIT_ID_2 );
        assertEquals( 3, nextRunNumber );
    }


    @Test
    public void getMapByCommitAndRunNumber() {

        LOG.info( "\n===RunDaoTest.getMapByCommitAndRunNumber===\n" );

        Map<String, Run> runs = ESSuiteTest.runDao.getMap( ESSuiteTest.COMMIT_ID_2, 2, ESSuiteTest.TEST_NAME );

        for ( String runId : runs.keySet() ) {
            LOG.info("{}: {}", runId, runs.get( runId ));
        }

        // @todo assert Equals size
    }

}
