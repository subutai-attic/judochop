package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicProviderParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class ProviderParamsDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger( ProviderParamsDaoTest.class );

    @Inject
    private ProviderParamsDao ppDao = null;


    @Test
    public void save() throws Exception {

        ProviderParams pp = new BasicProviderParams(
                "user",
                "t1.micro",
                "es-east",
                "1230d4353459da23ec21a259a",
                "ad911213ab21ef23ab4e0e",
                "ami-213213214",
                "chop-security",
                "Ec2KeyPair",
                "chop-runner"
        );

        boolean created = ppDao.save( pp );

        LOG.warn( created + ": " + pp );
    }


    @Test
    public void testGetAll() throws Exception {

        List<ProviderParams> list = ppDao.getAll();

        for (ProviderParams pp : list) {
            LOG.warn( pp.toString() );
        }

        LOG.warn( "count: " + list.size() );
    }


    @Test
    public void testGetByUsername() {
        LOG.warn( ppDao.getByUser( "user2" ).toString() );
    }

}
