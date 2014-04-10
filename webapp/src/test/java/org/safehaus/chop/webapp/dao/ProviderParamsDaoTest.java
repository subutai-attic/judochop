package org.safehaus.chop.webapp.dao;

import org.junit.Test;
import org.apache.usergrid.chop.api.ProviderParams;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ProviderParamsDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger( ProviderParamsDaoTest.class );


    @Test
    public void testGetAll() throws Exception {

        LOG.info( "\n===ProviderParamsDaoTest.testGetAll===\n" );

        List<ProviderParams> list = ESSuiteTest.ppDao.getAll();

        for ( ProviderParams pp : list ) {
            LOG.info( pp.toString() );
        }

        assertEquals( 2, list.size() );
    }


    @Test
    public void testGetByUsername() {

        LOG.info( "\n===ProviderParamsDaoTest.testGetByUsername===\n" );

        ProviderParams pp = ESSuiteTest.ppDao.getByUser( ESSuiteTest.USER_1 );

        assertEquals( ESSuiteTest.IMAGE_ID, pp.getImageId() );
    }

}
