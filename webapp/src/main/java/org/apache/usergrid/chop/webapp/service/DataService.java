package org.apache.usergrid.chop.webapp.service;

import com.google.inject.Inject;
<<<<<<< HEAD:webapp/src/main/java/org/safehaus/chop/webapp/service/DataService.java
import org.apache.usergrid.chop.api.Commit;
import org.safehaus.chop.webapp.dao.*;
=======
import org.apache.usergrid.chop.webapp.dao.CommitDao;
import org.apache.usergrid.chop.webapp.dao.RunDao;
import org.safehaus.chop.api.*;
>>>>>>> dacd615ebeaae3a0dac592cb7f0367c81e8f4b22:webapp/src/main/java/org/apache/usergrid/chop/webapp/service/DataService.java

import java.util.List;
import java.util.Set;

public class DataService {

    @Inject
    private RunDao runDao = null;

    @Inject
    private CommitDao commitDao = null;

    public Set<String> getTestNames(String moduleId) {
        List<Commit> commits = commitDao.getByModule(moduleId);
        return runDao.getTestNames(commits);
    }

}
