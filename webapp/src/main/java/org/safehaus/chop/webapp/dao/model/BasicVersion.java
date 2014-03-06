package org.safehaus.chop.webapp.dao.model;

import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Version;

import java.util.Date;
import java.util.UUID;

public class BasicVersion implements Version {

    private UUID commitId;
    private Module module;

    public BasicVersion(UUID commitId, Module module) {
        this.commitId = commitId;
        this.module = module;
    }

    @Override
    public UUID getCommitId() {
        return commitId;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getWarMd5() {
        return null;
    }

    @Override
    public Date getCreateTime() {
        return null;  
    }
}
