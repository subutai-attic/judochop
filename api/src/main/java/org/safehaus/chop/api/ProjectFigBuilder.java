package org.safehaus.chop.api;


import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Properties;

import org.safehaus.guicyfig.Bypass;
import org.safehaus.guicyfig.OptionState;
import org.safehaus.guicyfig.Overrides;

import org.apache.commons.lang.NotImplementedException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;


/**
 * Builds a ProjectFig for use many by the plugin.
 */
public class ProjectFigBuilder {
    private Properties props;
    private ProjectFig supplied;
    private String testPackageBase;
    private String createTimestamp;
    private String artifactId;
    private String version;
    private String groupId;
    private String vcsRepoUrl;
    private String commitId;
    private String loadKey;
    private String chopVersion;
    private String warMd5;
    private String loadTime;
    private String managerUsername;
    private String managerPassword;
    private String managerEndpoint;


    public ProjectFigBuilder() {
        // do nothing - supplied will be injected if in Guice env
    }


    public ProjectFigBuilder( Properties props ) {
        this.props = props;
        updateValues();
    }


    public ProjectFigBuilder( ProjectFig project ) {
        // set the supplied project - this is manually provided
        this.supplied = project;
        updateValues();
    }


    @Inject
    public void setProject( ProjectFig project ) {
        if ( supplied == null ) {
            supplied = project;
            updateValues();
        }
    }


    private void updateValues() {
        if ( supplied != null ) {
            this.testPackageBase = supplied.getTestPackageBase();
            this.createTimestamp = supplied.getCreateTimestamp();
            this.artifactId = supplied.getArtifactId();
            this.version = supplied.getVersion();
            this.groupId = supplied.getGroupId();
            this.vcsRepoUrl = supplied.getVcsRepoUrl();
            this.commitId = supplied.getVcsVersion();
            this.loadKey = supplied.getLoadKey();
            this.chopVersion = supplied.getChopVersion();
            this.warMd5 = supplied.getWarMd5();
            this.loadTime = supplied.getLoadTime();
            this.managerPassword = supplied.getManagerPassword();
            this.managerUsername = supplied.getManagerUsername();
            this.managerEndpoint = supplied.getManagerEndpoint();
        }

        if ( props != null ) {
            if ( props.containsKey( ProjectFig.LOAD_TIME_KEY ) ) {
                this.loadTime = props.getProperty( ProjectFig.LOAD_TIME_KEY );
            }

            if ( props.containsKey( ProjectFig.LOAD_KEY ) ) {
                this.loadKey = props.getProperty( ProjectFig.LOAD_KEY );
            }

            if ( props.containsKey( ProjectFig.ARTIFACT_ID_KEY ) ) {
                this.artifactId = props.getProperty( ProjectFig.ARTIFACT_ID_KEY );
            }

            if ( props.containsKey( ProjectFig.CHOP_VERSION_KEY ) ) {
                this.chopVersion = props.getProperty( ProjectFig.CHOP_VERSION_KEY );
            }

            if ( props.containsKey( ProjectFig.CREATE_TIMESTAMP_KEY ) ) {
                this.createTimestamp = props.getProperty( ProjectFig.CREATE_TIMESTAMP_KEY );
            }

            if ( props.containsKey( ProjectFig.GIT_URL_KEY ) ) {
                this.vcsRepoUrl = props.getProperty( ProjectFig.GIT_URL_KEY );
            }

            if ( props.containsKey( ProjectFig.GIT_UUID_KEY ) ) {
                this.commitId = props.getProperty( ProjectFig.GIT_UUID_KEY );
            }

            if ( props.containsKey( ProjectFig.GROUP_ID_KEY ) ) {
                this.groupId = props.getProperty( ProjectFig.GROUP_ID_KEY );
            }

            if ( props.containsKey( ProjectFig.MANAGER_PASSWORD_KEY ) ) {
                this.managerPassword = props.getProperty( ProjectFig.MANAGER_PASSWORD_KEY );
            }

            if ( props.containsKey( ProjectFig.MANAGER_USERNAME_KEY ) ) {
                this.managerUsername = props.getProperty( ProjectFig.MANAGER_USERNAME_KEY );
            }

            if ( props.containsKey( ProjectFig.MANAGER_ENDPOINT_KEY ) ) {
                this.managerEndpoint = props.getProperty( ProjectFig.MANAGER_ENDPOINT_KEY );
            }

            if ( props.containsKey( ProjectFig.PROJECT_VERSION_KEY ) ) {
                this.version = props.getProperty( ProjectFig.PROJECT_VERSION_KEY );
            }

            if ( props.containsKey( ProjectFig.TEST_PACKAGE_BASE ) ) {
                this.testPackageBase = props.getProperty( ProjectFig.TEST_PACKAGE_BASE );
            }

            if ( props.containsKey( ProjectFig.WAR_MD5_KEY ) ) {
                this.warMd5 = props.getProperty( ProjectFig.WAR_MD5_KEY );
            }
        }
    }


    @JsonProperty
    public ProjectFigBuilder setTestPackageBase( final String testPackageBase ) {
        this.testPackageBase = testPackageBase;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setCreateTimestamp( final String timeStamp ) {
        this.createTimestamp = timeStamp;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setArtifactId( final String artifactId ) {
        this.artifactId = artifactId;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setProjectVersion( final String version ) {
        this.version = version;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setGroupId( final String groupId ) {
        this.groupId = groupId;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setVcsRepoUrl( final String vcsRepoUrl ) {
        this.vcsRepoUrl = vcsRepoUrl;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setVcsVersion( final String commitId ) {
        this.commitId = commitId;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setLoadKey( final String loadKey ) {
        this.loadKey = loadKey;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setChopVersion( final String version ) {
        this.chopVersion = version;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setWarMd5( final String warMd5 ) {
        this.warMd5 = warMd5;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setLoadTime( final String loadTime ) {
        this.loadTime = loadTime;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setManagerUsername( final String managerUsername ) {
        this.managerUsername = managerUsername;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setManagerPassword( final String managerPassword ) {
        this.managerPassword = managerPassword;
        return this;
    }


    @JsonProperty
    public ProjectFigBuilder setManagerEndpoint( final String managerEndpoint ) {
        this.managerEndpoint = managerEndpoint;
        return this;
    }


    public ProjectFig getProject() {
        return new ProjectFig() {
            @Override
            public String getChopVersion() {
                return chopVersion;
            }


            @Override
            public String getCreateTimestamp() {
                return createTimestamp;
            }


            @Override
            public String getVcsVersion() {
                return commitId;
            }


            @Override
            public String getVcsRepoUrl() {
                return vcsRepoUrl;
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
            public String getTestPackageBase() {
                return testPackageBase;
            }


            @Override
            public long getTestStopTimeout() {
                return Long.parseLong( DEFAULT_TEST_STOP_TIMEOUT );
            }


            @Override
            public String getLoadTime() {
                return loadTime;
            }


            @Override
            public String getLoadKey() {
                return loadKey;
            }


            @Override
            public String getWarMd5() {
                return warMd5;
            }


            @Override
            public String getManagerUsername() {
                return managerUsername;
            }


            @Override
            public String getManagerPassword() {
                return managerPassword;
            }


            @Override
            public String getManagerEndpoint() {
                return managerEndpoint;
            }


            @JsonIgnore
            @Override
            public void addPropertyChangeListener( final PropertyChangeListener listener ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public void removePropertyChangeListener( final PropertyChangeListener listener ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public OptionState[] getOptions() {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public OptionState getOption( final String key ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public String getKeyByMethod( final String methodName ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Object getValueByMethod( final String methodName ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Properties filterOptions( final Properties properties ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Map<String, Object> filterOptions( final Map<String, Object> entries ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public void override( final String key, final String override ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public boolean setOverrides( final Overrides overrides ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Overrides getOverrides() {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public void bypass( final String key, final String bypass ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public boolean setBypass( final Bypass bypass ) {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Bypass getBypass() {
                throw new NotImplementedException();
            }


            @JsonIgnore
            @Override
            public Class getFigInterface() {
                return ProjectFig.class;
            }


            @JsonIgnore
            @Override
            public boolean isSingleton() {
                return false;
            }
        };
    }
}
