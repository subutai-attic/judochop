package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class RunnerDaoTest {

    private static Logger LOG = LoggerFactory.getLogger( RunnerDaoTest.class );


    @Test
    public void delete() {
        //System.out.println( runnerDao.delete("localhost") );
    }

    @Test
    public void getRunners() {
        //System.out.println( runnerDao.getRunners("commitId") );
    }

}
