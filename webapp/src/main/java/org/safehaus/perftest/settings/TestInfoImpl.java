package org.safehaus.perftest.settings;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Module;
import org.safehaus.perftest.Perftest;
import org.safehaus.perftest.RunInfo;
import org.safehaus.perftest.TestInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Test specific information.
 */
public class TestInfoImpl implements TestInfo {
    private final Perftest userPerftest;
    private final Module userModule;
    private final String perftestVersion = PropSettings.getPerftestVersion();
    private final List<RunInfo> runInfos = new ArrayList<RunInfo>();
    private final String perftestFormation = PropSettings.getFormation();
    private final String createTimestamp = PropSettings.getCreateTimestamp();
    private final String gitUuid = PropSettings.getGitUuid();
    private final String getGitRepoUrl = PropSettings.getGitUrl();
    private final String getGroupId = PropSettings.getGroupId();
    private final String getArtifactId = PropSettings.getArtifactId();
    private final String loadKey;
    private String loadTime;


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
    public String getPerftestFormation() {
        return perftestFormation;
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
    @SuppressWarnings("UnusedDeclaration")
    public void setLoadTime( String loadTime ) {
        this.loadTime = loadTime;
    }
}
