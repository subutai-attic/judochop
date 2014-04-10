package org.apache.usergrid.chop.webapp.service;

import com.google.inject.Inject;
import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.webapp.dao.CommitDao;
import org.apache.usergrid.chop.webapp.dao.RunDao;

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
