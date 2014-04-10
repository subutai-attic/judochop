package org.apache.usergrid.chop.webapp.dao;

import org.junit.Test;
<<<<<<< HEAD:webapp/src/test/java/org/safehaus/chop/webapp/dao/ProviderParamsDaoTest.java
import org.apache.usergrid.chop.api.ProviderParams;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
=======
import org.safehaus.chop.api.ProviderParams;
import org.apache.usergrid.chop.webapp.elasticsearch.ESSuiteTest;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/test/java/org/apache/usergrid/chop/webapp/dao/ProviderParamsDaoTest.java
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
