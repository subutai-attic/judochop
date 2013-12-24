package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * Information about the project to be chopped up!
 */
public class Project implements ConfigKeys {
    private String testPackageBase;
    private String chopVersion;
    private String createTimestamp;
    private String vcsVersion;
    private String vcsRepoUrl;
    private String groupId;
    private String artifactId;
    private String projectVersion;
    private String warMd5;
    private String loadKey;
    private String loadTime;


    @Inject
    public Project() {
    }


    @JsonProperty
    public String getTestPackageBase() {
        return testPackageBase;
    }


    @JsonProperty
    public String getChopVersion() {
        return chopVersion;
    }


    @JsonProperty
    public String getCreateTimestamp() {
        return createTimestamp;
    }


    @JsonProperty
    public String getVcsVersion() {
        return vcsVersion;
    }


    @JsonProperty
    public String getVcsRepoUrl() {
        return vcsRepoUrl;
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
    public void setChopVersion( @Named( CHOP_VERSION_KEY ) String chopVersion ) {
        this.chopVersion = chopVersion;
    }


    @Inject
    public void setCreateTimestamp( @Named( CREATE_TIMESTAMP_KEY ) String createTimestamp ) {
        this.createTimestamp = createTimestamp;
    }


    @Inject
    public void setVcsVersion( @Named( GIT_UUID_KEY ) String vcsVersion ) {
        this.vcsVersion = vcsVersion;
    }


    @Inject
    public void setVcsRepoUrl( @Named( GIT_URL_KEY ) String getGitRepoUrl ) {
        this.vcsRepoUrl = getGitRepoUrl;
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
    public void setTestPackageBase( @Named( TEST_PACKAGE_BASE ) String testPackageBase ) {
        this.testPackageBase = testPackageBase;
    }


    @Inject
    public void setLoadKey( @Named( LOAD_KEY ) String loadKey ) {
        this.loadKey = loadKey;
    }


    @JsonProperty
    public String getProjectVersion() {
        return projectVersion;
    }


    @Inject
    public void setProjectVersion( @Named( PROJECT_VERSION_KEY ) final String projectVersion ) {
        this.projectVersion = projectVersion;
    }


    @JsonProperty
    public String getWarMd5() {
        return warMd5;
    }

    @Inject
    public void setWarMd5( @Named( WAR_MD5_KEY ) final String warMd5 ) {
        this.warMd5 = warMd5;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( "\nProject {\n\n\t" );

        if ( testPackageBase != null ) {
            sb.append( TEST_PACKAGE_BASE ).append( ": " ).append( testPackageBase ).append( "\n\t" );
        }

        if ( chopVersion != null ) {
            sb.append( CHOP_VERSION_KEY ).append( ": " ).append( chopVersion ).append( "\n\t" );
        }

        if ( createTimestamp != null ) {
            sb.append( CREATE_TIMESTAMP_KEY ).append( ": " ).append( createTimestamp ).append( "\n\t" );
        }

        if ( vcsVersion != null ) {
            sb.append( GIT_UUID_KEY ).append( ": " ).append( vcsVersion ).append( "\n\t" );
        }

        if ( vcsRepoUrl != null ) {
            sb.append( GIT_URL_KEY ).append( ": " ).append( vcsRepoUrl ).append( "\n\t" );
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
