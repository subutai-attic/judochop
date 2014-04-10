package org.apache.usergrid.chop.webapp.dao;

import java.util.List;

import org.junit.Test;
import org.apache.usergrid.chop.stack.User;
import org.apache.usergrid.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;


public class UserDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( UserDaoTest.class );


    @Test
    public void getAll() {

        LOG.info( "\n===UserDaoTest.getAll===\n" );

        List<User> users = ESSuiteTest.userDao.getList();

        for( User user : users ) {
            LOG.info( "User is: {}", user.toString() );
        }

    }


    @Test
    public void get() {

        LOG.info( "\n===UserDaoTest.get===\n" );

        User user = ESSuiteTest.userDao.get( ESSuiteTest.USER_1 );

        LOG.info( "User is: {}", user.toString() );

        assertEquals( "password", user.getPassword() );
    }


    @Test
    public void delete() {

        LOG.info( "\n===UserDaoTest.delete===\n" );

        LOG.info( "Users before delete: " );

        List<User> users = ESSuiteTest.userDao.getList();

        for( User user : users ) {
            LOG.info( "    {}", user.toString() );
        }

        ESSuiteTest.userDao.delete( ESSuiteTest.USER_2 );

        LOG.info( "Users after delete: " );

        users = ESSuiteTest.userDao.getList();

        for( User user : users ) {
            LOG.info( "    {}", user.toString() );
        }

        assertEquals( 1, users.size() );
    }


}
