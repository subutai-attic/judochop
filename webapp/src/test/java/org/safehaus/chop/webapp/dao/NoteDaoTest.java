package org.safehaus.chop.webapp.dao;

import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.Note;
import org.safehaus.chop.webapp.elasticsearch.ESSuiteTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

//@RunWith(JukitoRunner.class)
//@UseModules(ChopUiModule.class)
public class NoteDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( NoteDaoTest.class );


    @Test
    public void testGet() {
        Note note = ESSuiteTest.noteDao.get( ESSuiteTest.COMMIT_ID_1, 1 );

        assertEquals( ESSuiteTest.NOTE, note.getText() );
    }
}
