package org.safehaus.chop.api;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Information about the project to be chopped up!
 */
@FigSingleton
@JsonSerialize( using = ProjectSerializer.class )
@JsonDeserialize( using = ProjectDeserializer.class )
public interface Project extends GuicyFig {

    // ~~~~~~~~~~~~~~~~~~~~ Chopped Project's Parameters ~~~~~~~~~~~~~~~~~~~~


    String CHOP_VERSION_KEY = "chop.version";

    /**
     * Gets the version of Judo Chop used to generate Runners. Uses {@link
     * Project#CHOP_VERSION_KEY} to access the chop version used.
     *
     * @return the Judo Chop version
     */
    @JsonProperty
    @Key( CHOP_VERSION_KEY )
    String getChopVersion();


    String CREATE_TIMESTAMP_KEY = "create.timestamp";

    /**
     * Gets the timestamp when the project Runner was generated. Uses
     * {@link Project#CREATE_TIMESTAMP_KEY} to access the Runner creation
     * timestamp.
     *
     * @return the Judo Chop runner creation time
     */
    @JsonProperty
    @Key( CREATE_TIMESTAMP_KEY )
    String getCreateTimestamp();


    String GIT_UUID_KEY = "git.uuid";

    /**
     * The last commit id of the project being chopped. Uses {@link
     * Project#GIT_URL_KEY} to access the commit identifier.
     *
     * @return the commit identifier
     */
    @JsonProperty
    @Key( GIT_UUID_KEY )
    String getVcsVersion();


    String GIT_URL_KEY = "git.url";

    /**
     * Gets the version control system URL for the project. Uses
     * {@link Project#GIT_URL_KEY} to access the VCS URL for the Project.
     *
     * @return the VCS URL for this project
     */
    @JsonProperty
    @Key( GIT_URL_KEY )
    String getVcsRepoUrl();


    String GROUP_ID_KEY = "group.id";

    /**
     * Gets the Maven project group identifier or equivalent for the project.
     * Uses {@link Project#GROUP_ID_KEY} to access the Maven group id.
     *
     * @return the Maven group id
     */
    @JsonProperty
    @Key( GROUP_ID_KEY )
    String getGroupId();


    String ARTIFACT_ID_KEY = "artifact.id";

    /**
     * Gets the Maven artifact identifier or equivalent associated for the project.
     * Uses {@link Project#ARTIFACT_ID_KEY} to access the Maven artifact id.
     *
     * @return the Maven artifact id
     */
    @JsonProperty
    @Key( ARTIFACT_ID_KEY )
    String getArtifactId();


    String PROJECT_VERSION_KEY = "project.version";

    /**
     * Gets the Maven version of the project being chopped. Uses {@link
     * Project#PROJECT_VERSION_KEY} to access the Maven project version.
     *
     * @return the Maven project version
     */
    @JsonProperty
    @Key( PROJECT_VERSION_KEY )
    String getVersion();


    String TEST_PACKAGE_BASE = "test.package.base";

    /**
     * Gets the package base of the tests to run.
     *
     * @return the package base of the tests
     */
    @JsonProperty
    @Key( TEST_PACKAGE_BASE )
    String getTestPackageBase();


    // ~~~~~~~~~~~~~~~~~~~~~ Runner Related Configuration ~~~~~~~~~~~~~~~~~~~~


    String TEST_STOP_TIMEOUT = "test.stop.timeout";
    String DEFAULT_TEST_STOP_TIMEOUT = "1000";

    /**
     * Gets the time in milliseconds to wait until a test stops. Uses {@link
     * Project#TEST_STOP_TIMEOUT} to access the test timeout parameter.
     *
     * @return the time in milliseconds to wait for a test to stop
     */
    @JsonProperty
    @Key( TEST_STOP_TIMEOUT )
    @Default( DEFAULT_TEST_STOP_TIMEOUT )
    long getTestStopTimeout();


    String LOAD_TIME_KEY = "load.time.key";

    /**
     * Gets the load timestamp for when the runner was loaded. Uses {@link
     * Project#LOAD_TIME_KEY} to access the load timestamp.
     *
     * @return the load timestamp of the runner
     */
    @JsonProperty
    @Key( LOAD_TIME_KEY )
    String getLoadTime();


    /** Key used to locate the runner's war in the store. */
    String LOAD_KEY = "load.key";

    /**
     * Gets the path or key to the Runner's war in the store. Uses {@link
     * Project#LOAD_KEY} to access the store's load key.
     *
     * @return the path or key to the Runner's war in the store
     */
    @JsonProperty
    @Key( LOAD_KEY )
    String getLoadKey();



    String MD5_KEY = "md5";

    /**
     * Gets the MD5 checksum of the Runner. Uses {@link Project#MD5_KEY} to
     * access the MD5 checksum.
     *
     * @return the MD5 checksum of the Runner
     */
    @JsonProperty
    @Key( MD5_KEY )
    String getMd5();


    String MANAGER_USERNAME_KEY = "manager.app.username";

    /**
     * Gets the username to use with the Tomcat manager of this Runner. Uses {@link
     * Project#MANAGER_USERNAME_KEY} to access the Tomcat manager username.
     *
     * @return the Tomcat manager username
     */
    @JsonProperty
    @Key( MANAGER_USERNAME_KEY )
    String getManagerUsername();


    String MANAGER_PASSWORD_KEY = "manager.app.password";

    /**
     * Gets the Tomcat manager user's password. Uses {@link Project#MANAGER_PASSWORD_KEY}
     * to access the Tomcat manager user's password.
     *
     * @return the Tomcat manager user's password
     */
    @JsonProperty
    @Key( MANAGER_PASSWORD_KEY )
    String getManagerPassword();


    String MANAGER_ENDPOINT_KEY = "manager.endpoint";
    String DEFAULT_MANAGER_ENDPOINT = "http://localhost:8080/manager/text";

    /**
     * Gets the Tomcat manager base URL.
     *
     * @return the Tomcat Manager endpoint
     */
    @JsonProperty
    @Key( MANAGER_ENDPOINT_KEY )
    @Default( DEFAULT_MANAGER_ENDPOINT )
    String getManagerEndpoint();
}
