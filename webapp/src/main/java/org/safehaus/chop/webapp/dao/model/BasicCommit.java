package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;

import java.util.Date;

public class BasicCommit implements Commit {

    private String commitId;
    private Module module;
    private String warMd5;
    private Date createTime;

    public BasicCommit(String commitId, Module module, String warMd5, Date createTime) {
        this.commitId = commitId;
        this.module = module;
        this.warMd5 = warMd5;
        this.createTime = createTime;
    }

    @Override
    public String getId() {
        // For this simple example we can use just commitId. In real app there should generated more unique id.
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
