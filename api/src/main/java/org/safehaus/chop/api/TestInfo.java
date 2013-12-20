package org.safehaus.chop.api;


import org.safehaus.chop.api.settings.ConfigKeys;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/7/13 Time: 12:57 AM To change this template use File | Settings
 * | File Templates.
 */
public class TestInfo implements ConfigKeys {
    private String testModuleFQCN;
    private String perftestVersion;
    private String createTimestamp;
    private String gitUuid;
    private String gitRepoUrl;
    private String groupId;
    private String artifactId;
    private String projectVersion;
    private String warMd5;
    private String loadKey;
    private String loadTime;


    @Inject
    public TestInfo() {
    }


    @JsonProperty
    public String getTestModuleFQCN() {
        return testModuleFQCN;
    }


    @JsonProperty
    public String getPerftestVersion() {
        return perftestVersion;
    }


    @JsonProperty
    public String getCreateTimestamp() {
        return createTimestamp;
    }


    @JsonProperty
    public String getGitUuid() {
        return gitUuid;
    }


    @JsonProperty
    public String getGitRepoUrl() {
        return gitRepoUrl;
    }


    @JsonProperty
    public String getGroupId() {
        return groupId;
    }


    @JsonProperty
    public String getArtifactId() {
        return artifactId;
    }


    @JsonProperty
    public String getLoadKey() {
        return loadKey;
    }


    @JsonProperty
    public String getLoadTime() {
        return loadTime;
    }


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
    public void setGitRepoUrl( @Named( GIT_URL_KEY ) String getGitRepoUrl ) {
        this.gitRepoUrl = getGitRepoUrl;
    }


    @Inject
    public void setGroupId( @Named( GROUP_ID_KEY ) String getGroupId ) {
        this.groupId = getGroupId;
    }


    @Inject
    public void setArtifactId( @Named( ARTIFACT_ID_KEY ) String getArtifactId ) {
        this.artifactId = getArtifactId;
    }


    @Inject
    public void setTestModuleFQCN( @Named( TEST_MODULE_FQCN_KEY ) String testModuleFQCN ) {
        this.testModuleFQCN = testModuleFQCN;
    }


    @Inject
    public void setLoadKey( @Named( LOAD_KEY ) String loadKey ) {
        this.loadKey = loadKey;
    }


    @JsonProperty
    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion( final String projectVersion ) {
        this.projectVersion = projectVersion;
    }


    @JsonProperty
    public String getWarMd5() {
        return warMd5;
    }


    public void setWarMd5( final String warMd5 ) {
        this.warMd5 = warMd5;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( "\nTestInfo {\n\n\t" );

        if ( testModuleFQCN != null ) {
            sb.append( TEST_MODULE_FQCN_KEY ).append( ": " ).append( testModuleFQCN ).append( "\n\t" );
        }

        if ( perftestVersion != null ) {
            sb.append( PERFTEST_VERSION_KEY ).append( ": " ).append( perftestVersion ).append( "\n\t" );
        }

        if ( createTimestamp != null ) {
            sb.append( CREATE_TIMESTAMP_KEY ).append( ": " ).append( createTimestamp ).append( "\n\t" );
        }

        if ( gitUuid != null ) {
            sb.append( GIT_UUID_KEY ).append( ": " ).append( gitUuid ).append( "\n\t" );
        }

        if ( gitRepoUrl != null ) {
            sb.append( GIT_URL_KEY ).append( ": " ).append( gitRepoUrl ).append( "\n\t" );
        }

        if ( groupId != null ) {
            sb.append( GROUP_ID_KEY ).append( ": " ).append( groupId ).append( "\n\t" );
        }

        if ( artifactId != null ) {
            sb.append( ARTIFACT_ID_KEY ).append( ": " ).append( artifactId ).append( "\n\t" );
        }

        if ( projectVersion != null ) {
            sb.append( PROJECT_VERSION_KEY ).append( ": " ).append( projectVersion ).append( "\n\t" );
        }

        if ( warMd5 != null ) {
            sb.append( WAR_MD5_KEY ).append( ": " ).append( warMd5 ).append( "\n\t" );
        }

        if ( loadKey != null ) {
            sb.append( LOAD_KEY ).append( ": " ).append( loadKey ).append( "\n\t" );
        }

        if ( loadTime != null ) {
            sb.append( LOAD_TIME_KEY ).append( ": " ).append( loadTime ).append( "\n" );
        }

        sb.append( "\n}" );

        return sb.toString();
    }
}
