package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.*;
import org.safehaus.chop.webapp.dao.*;

public class RunService {

    @Inject
    private RunDao dao;

    public boolean save(Run run) throws Exception {
        return dao.save(run);
    }

}
