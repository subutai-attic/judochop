package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Version;

import java.util.Date;
import java.util.UUID;

public class BasicVersion implements Version {

    private String commitId;
    private Module module;
    private String warMd5;
    private Date createTime;

    public BasicVersion(String commitId, Module module, String warMd5, Date createTime) {
        this.commitId = commitId;
        this.module = module;
        this.warMd5 = warMd5;
        this.createTime = createTime;
    }

    @Override
    public String getId() {
        return commitId;
    }

    @Override
    public String getCommitId() {
        return commitId;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getWarMd5() {
        return warMd5;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("moduleId", module.getId())
                .append("commitId", commitId)
                .append("warMd5", warMd5)
                .append("createTime", createTime)
                .toString();
    }
}
