package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.*;
import org.safehaus.chop.webapp.dao.*;

public class ModuleService {

    @Inject
    private ModuleDao dao;

    public Module get(String id) {
        return dao.get(id);
    }

    public boolean save(Module module) throws Exception {
        return dao.save(module);
    }

}
