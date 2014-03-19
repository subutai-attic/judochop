package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.dao.model.User;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class UserDaoTest {

    @Inject
    private UserDao userDao;

    @Test
    public void save() throws Exception {
        User user = new User( "testuser", "password" );

        boolean created = userDao.save( user );

        System.out.println( created + ": " + user );
    }

    @Test
    public void get() {
        System.out.println( userDao.get( "testuser" ) );
    }

    @Test
    public void delete() {
        System.out.println( userDao.delete( "testuser" ) );
    }

    @Test
    public void getList() {
        System.out.println( userDao.getList() );
    }
}
