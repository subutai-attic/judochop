package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Module;

public class BasicModule implements Module {

    private String id;
    private String groupId;
    private String artifactId;
    private String version;
    private String vcsRepoUrl;
    private String testPackageBase;

    public BasicModule(String groupId, String artifactId, String version, String vcsRepoUrl, String testPackageBase) {
        id = createId(groupId, artifactId, version);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.vcsRepoUrl = vcsRepoUrl;
        this.testPackageBase = testPackageBase;
    }

    @Override
    public String getId() {
        return id;
    }

    public static String createId( String groupId, String artifactId, String version ) {
        return "" + new HashCodeBuilder()
                .append(groupId)
                .append(artifactId)
                .append(version)
                .toHashCode();
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getVcsRepoUrl() {
        return vcsRepoUrl;
    }

    @Override
    public String getTestPackageBase() {
        return testPackageBase;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("groupId", groupId)
                .append("artifactId", artifactId)
                .append("version", version)
                .append("vcsRepoUrl", vcsRepoUrl)
                .append("testPackageBase", testPackageBase)
                .toString();
    }
}
