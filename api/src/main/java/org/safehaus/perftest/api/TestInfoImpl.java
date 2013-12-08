package org.safehaus.perftest.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.netflix.config.DynamicStringProperty;

import java.util.ArrayList;
import java.util.List;

import org.safehaus.perftest.api.settings.ConfigKeys;


/**
 * Test specific information.
 */
public class TestInfoImpl implements TestInfo, ConfigKeys {
    private final Perftest userPerftest;
    private final Module userModule;

    @Inject @Named( PERFTEST_VERSION_KEY ) private DynamicStringProperty perftestVersion;
    @Inject @Named( CREATE_TIMESTAMP_KEY ) private DynamicStringProperty createTimestamp;
    @Inject @Named( GIT_UUID_KEY ) private DynamicStringProperty gitUuid;
    @Inject @Named( GIT_URL_KEY ) private DynamicStringProperty getGitRepoUrl;
    @Inject @Named( GROUP_ID_KEY ) private DynamicStringProperty getGroupId;
    @Inject @Named( ARTIFACT_ID_KEY ) private DynamicStringProperty getArtifactId;

    private final List<RunInfo> runInfos = new ArrayList<RunInfo>();
    private final String loadKey;
    private String loadTime;


    @Inject
    public TestInfoImpl( Perftest userPerftest, Module userModule ) {
        this.userPerftest = userPerftest;
        this.userModule = userModule;

        StringBuilder sb = new StringBuilder();
        sb.append( "tests/" )
                .append(gitUuid)
                .append('-')
                .append( createTimestamp )
                .append( '/' )
                .append( "perftest.war" );
        loadKey = sb.toString();
    }


    @Override
    @JsonProperty
    public Perftest getUserPerftest() {
        return userPerftest;
    }


    @Override
    @JsonProperty
    public String getUserModuleFQCN() {
        return userModule.getClass().getCanonicalName();
    }


    @Override
    @JsonProperty
    public String getPerftestVersion() {
        return perftestVersion.get();
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
        return createTimestamp.get();
    }


    @Override
    @JsonProperty
    public String getGitUuid() {
        return gitUuid.get();
    }


    @Override
    @JsonProperty
    public String getGetGitRepoUrl() {
        return getGitRepoUrl.get();
    }


    @Override
    @JsonProperty
    public String getGetGroupId() {
        return getGroupId.get();
    }


    @Override
    @JsonProperty
    public String getGetArtifactId() {
        return getArtifactId.get();
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
    @SuppressWarnings("UnusedDeclaration")
    public void setLoadTime( String loadTime ) {
        this.loadTime = loadTime;
    }
}
