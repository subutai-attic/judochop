package org.safehaus.perftest.api;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/7/13 Time: 12:57 AM To change this template use File | Settings
 * | File Templates.
 */
public interface TestInfo {
    @JsonProperty
    String getTestModuleFQCN();

    @JsonProperty
    String getPerftestVersion();

    @JsonProperty
    String getCreateTimestamp();

    @JsonProperty
    String getGitUuid();

    @JsonProperty
    String getGitRepoUrl();

    @JsonProperty
    String getGroupId();

    @JsonProperty
    String getProjectVersion();

    @JsonProperty
    String getArtifactId();

    @JsonProperty
    String getLoadKey();

    @JsonProperty
    String getLoadTime();

    @JsonProperty
    String getWarMd5();

    @SuppressWarnings("UnusedDeclaration")
    void setLoadTime( String loadTime );
}
