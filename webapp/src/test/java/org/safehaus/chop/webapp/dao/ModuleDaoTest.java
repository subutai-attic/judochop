package org.safehaus.chop.webapp.dao;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.webapp.ChopUiModule;
import org.safehaus.chop.webapp.dao.model.BasicModule;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(JukitoRunner.class)
@UseModules(ChopUiModule.class)
public class ModuleDaoTest {

    private static final String ID = "1168044208";

    @Inject
    private ModuleDao moduleDao = null;

    @Test
    public void save() throws Exception {

        Module module = new BasicModule(
                "org.apache.usergrid", // groupId
                "collection", // artifactId
                "1.0-SNAPSHOT", // version
                "https://github.com/usergrid/usergrid.git", // vcsRepoUrl
                "org.apache.usergrid" // testPackageBase
        );

        boolean created = moduleDao.save(module);

        System.out.println(created + ": " + module);
    }

    @Test
    public void getAll() throws Exception {
        List<Module> modules = moduleDao.getAll();

        for (Module m : modules) {
            System.out.println(m);
        }

        System.out.println("count: " + modules.size());
    }

    @Test
    public void get() {
        System.out.println( moduleDao.get(ID) );
    }

    @Test
    public void forTreeTable() throws Exception {
        List<Module> modules = moduleDao.getAll();

        for (Module module : modules) {
            String id = String.format( "%s-%s", module.getGroupId(), module.getArtifactId() );
            System.out.println( id );
        }

//        treeTable.addItem(new Object[]{ "Module1", "v1"}, "id1");
//        treeTable.addItem(new Object[]{"version1", "v3"}, 3);
//
//        treeTable.setParent(3, "id1");



    }

}
