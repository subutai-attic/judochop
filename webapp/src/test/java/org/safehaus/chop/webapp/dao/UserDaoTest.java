package org.safehaus.chop.webapp.dao;

import java.util.List;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.User;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;


//@RunWith(JukitoRunner.class)
//@UseModules(ChopUiModule.class)
public class UserDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( UserDaoTest.class );


    @Test
    public void get() {

        User user = ESSuiteTest.userDao.get( ESSuiteTest.USER_1 );

        assertEquals( "password", user.getPassword() );
    }


    @Test
    public void delete() {

        LOG.info( "Users before delete: " );

        List<User> users = ESSuiteTest.userDao.getList();

        for( User user : users ) {
            LOG.info( "    {}", user.toString() );
        }

        ESSuiteTest.userDao.delete( ESSuiteTest.USER_2 );

        LOG.info( "Users after delete: " );

        ESSuiteTest.userDao.getList();

        for( User user : users ) {
            LOG.info( "    {}", user.toString() );
        }

        assertEquals( 1, users.size() );
    }


}
