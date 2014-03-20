package org.safehaus.chop.webapp.dao;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


//@RunWith( JukitoRunner.class )
//@UseModules( ChopUiModule.class )
public class CommitDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( CommitDaoTest.class );


    @Test
    public void testGetByModule() {

        LOG.info( "\n===CommitDaoTest.testGetByModule===\n" );

        List<Commit> list = ESSuiteTest.commitDao.getByModule( ESSuiteTest.MODULE_ID_2 );

        for (Commit commit : list) {
            LOG.info( commit.toString() );
        }

        assertEquals( 2, list.size() );
    }


    @Test
    public void testGet() {

        LOG.info( "\n===CommitDaoTest.testGet===\n" );

        Commit commit = ESSuiteTest.commitDao.getByModule( ESSuiteTest.MODULE_ID_1 ).get( 0 );

        LOG.info( commit.toString() );

        assertEquals( ESSuiteTest.COMMIT_ID_1, commit.getId() );
    }
}
