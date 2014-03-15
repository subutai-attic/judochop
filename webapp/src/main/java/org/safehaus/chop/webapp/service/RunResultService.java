package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.*;
import org.safehaus.chop.webapp.dao.RunResultDao;

public class RunResultService {

    @Inject
    private RunResultDao dao;

    public boolean save(RunResult runResult) throws Exception {
        return dao.save(runResult);
    }

}
