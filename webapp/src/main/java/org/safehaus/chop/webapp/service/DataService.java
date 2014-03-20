package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.*;
import org.safehaus.chop.webapp.dao.*;

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
