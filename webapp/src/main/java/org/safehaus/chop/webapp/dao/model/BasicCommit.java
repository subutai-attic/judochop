package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;

import java.util.Date;

public class BasicCommit implements Commit {

    private String id;
    private String moduleId;
    private String warMd5;
    private Date createTime;

    public BasicCommit(String id, String moduleId, String warMd5, Date createTime) {
        this.id = id;
        this.moduleId = moduleId;
        this.warMd5 = warMd5;
        this.createTime = createTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public String getWarMd5() {
        return warMd5;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other != null
                && other instanceof BasicCommit
                && ( (BasicCommit) other ).getId().equals(id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("moduleId", moduleId)
                .append("warMd5", warMd5)
                .append("createTime", createTime)
                .toString();
    }


}
