package org.apache.usergrid.chop.webapp.dao;

import org.apache.usergrid.chop.webapp.elasticsearch.ESSuiteTest;
import org.junit.Test;
<<<<<<< HEAD:webapp/src/test/java/org/safehaus/chop/webapp/dao/ModuleDaoTest.java
import org.apache.usergrid.chop.api.Module;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
=======
import org.safehaus.chop.api.Module;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/test/java/org/apache/usergrid/chop/webapp/dao/ModuleDaoTest.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;


public class ModuleDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( ModuleDaoTest.class );


    @Test
    public void getAll() throws Exception {

        LOG.info( "\n===ModuleDaoTest.getAll===\n" );

        List<Module> modules = ESSuiteTest.moduleDao.getAll();

        for ( Module m : modules ) {
            LOG.info( m.toString() );
        }

        assertEquals( "Wrong number of modules in elasticsearch", 2, modules.size() );
    }

    @Test
    public void get() {

        LOG.info( "\n===ModuleDaoTest.get===\n" );

        Module module = ESSuiteTest.moduleDao.get( ESSuiteTest.MODULE_ID_1 );
        LOG.info( "Module by ID: {} is {}", ESSuiteTest.MODULE_ID_1, module.toString() );
        assertEquals( ESSuiteTest.MODULE_GROUPID, module.getGroupId() );
    }

}
