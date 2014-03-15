package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.*;
import org.safehaus.chop.webapp.dao.*;

public class CommitService {

    @Inject
    private CommitDao dao;

    public boolean save(Commit commit) throws Exception {
        return dao.save(commit);
    }

}
