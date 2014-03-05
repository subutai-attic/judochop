package org.safehaus.chop.webapp.dao;

import org.safehaus.chop.api.Module;

public class BasicModule implements Module {

    private String groupId;
    private String artifactId;
    private String version;

    public BasicModule(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
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
        return "";
    }

    @Override
    public String getTestPackageBase() {
        return "";
    }

}
