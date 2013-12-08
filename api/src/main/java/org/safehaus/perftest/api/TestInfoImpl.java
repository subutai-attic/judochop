package org.safehaus.perftest.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;

import org.safehaus.perftest.api.settings.ConfigKeys;


/**
 * Test specific information.
 */
public class TestInfoImpl implements TestInfo, ConfigKeys {
    private String testModuleFQCN;
    private String perftestVersion;
    private String createTimestamp;
    private String gitUuid;
    private String getGitRepoUrl;
    private String getGroupId;
    private String getArtifactId;
    private final List<RunInfo> runInfos = new ArrayList<RunInfo>();
    private String loadKey;
    private String loadTime;


    @Inject
    public TestInfoImpl() {
    }


    @Override
    @JsonProperty
    public String getTestModuleFQCN() {
        return testModuleFQCN;
    }


    @Override
    @JsonProperty
    public String getPerftestVersion() {
        return perftestVersion;
    }


    @Override
    @JsonProperty
    public List<RunInfo> getRunInfos() {
        return runInfos;
    }


    @Override
    public void addRunInfo( RunInfo runInfo ) {
        runInfos.add( runInfo );
    }


    @Override
    @JsonProperty
    public String getCreateTimestamp() {
        return createTimestamp;
    }


    @Override
    @JsonProperty
    public String getGitUuid() {
        return gitUuid;
    }


    @Override
    @JsonProperty
    public String getGetGitRepoUrl() {
        return getGitRepoUrl;
    }


    @Override
    @JsonProperty
    public String getGetGroupId() {
        return getGroupId;
    }


    @Override
    @JsonProperty
    public String getGetArtifactId() {
        return getArtifactId;
    }


    @Override
    @JsonProperty
    public String getLoadKey() {
        return loadKey;
    }


    @Override
    @JsonProperty
    public String getLoadTime() {
        return loadTime;
    }


    @Override
    @Inject
    public void setLoadTime( @Named( LOAD_TIME_KEY ) String loadTime ) {
        this.loadTime = loadTime;
    }


    @Inject
    public void setPerftestVersion( @Named( PERFTEST_VERSION_KEY ) String perftestVersion ) {
        this.perftestVersion = perftestVersion;
    }


    @Inject
    public void setCreateTimestamp( @Named( CREATE_TIMESTAMP_KEY ) String createTimestamp ) {
        this.createTimestamp = createTimestamp;
    }


    @Inject
    public void setGitUuid( @Named( GIT_UUID_KEY ) String gitUuid ) {
        this.gitUuid = gitUuid;
    }


    @Inject
    public void setGetGitRepoUrl( @Named( GIT_URL_KEY ) String getGitRepoUrl ) {
        this.getGitRepoUrl = getGitRepoUrl;
    }


    @Inject
    public void setGetGroupId( @Named( GROUP_ID_KEY ) String getGroupId ) {
        this.getGroupId = getGroupId;
    }


    @Inject
    public void setGetArtifactId( @Named( ARTIFACT_ID_KEY ) String getArtifactId ) {
        this.getArtifactId = getArtifactId;
    }


    @Inject
    public void setTestModuleFQCN( @Named( TEST_MODULE_FQCN_KEY ) String testModuleFQCN ) {
        this.testModuleFQCN = testModuleFQCN;
    }


    @Inject
    public void setLoadKey( @Named( LOAD_KEY ) String loadKey ) {
        this.loadKey = loadKey;
    }
}
