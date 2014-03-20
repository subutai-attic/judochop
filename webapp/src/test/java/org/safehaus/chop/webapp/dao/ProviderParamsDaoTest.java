package org.safehaus.chop.webapp.dao;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class ProviderParamsDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger( ProviderParamsDaoTest.class );


    @Test
    public void testGetAll() throws Exception {

        List<ProviderParams> list = ESSuiteTest.ppDao.getAll();

        for ( ProviderParams pp : list ) {
            LOG.info( pp.toString() );
        }

        assertEquals( 2, list.size() );
    }


    @Test
    public void testGetByUsername() {
        ProviderParams pp = ESSuiteTest.ppDao.getByUser( ESSuiteTest.USER_1 );

        assertEquals( ESSuiteTest.IMAGE_ID, pp.getImageId() );
    }

}
