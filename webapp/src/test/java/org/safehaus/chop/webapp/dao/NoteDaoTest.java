package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicCommit;
import org.safehaus.chop.webapp.dao.model.Note;

import java.util.Date;
import java.util.List;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class NoteDaoTest {

    @Inject
    private NoteDao noteDao;

    @Test
    public void save() throws Exception {

        Note note = new Note( "7072b85746a980bc5dd9923ccdc9e0ed8e4eb19e", 1, "This is a note" );

        boolean created = noteDao.save( note );
        System.out.println( created + ": " + note );
    }

    @Test
    public void testGet() {
        System.out.println( noteDao.get( "noteCommitId", 1 ) );
    }
}
