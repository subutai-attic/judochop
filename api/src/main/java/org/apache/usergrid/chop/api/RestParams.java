package org.apache.usergrid.chop.api;


/**
 * Parameters used by REST resources.
 */
public interface RestParams {
    String CONTENT = "content";
    String FILENAME = "file";
    String RUNNER_URL = "runnerUrl";
    String RUNNER_HOSTNAME = "runnerHostname";
    String RUNNER_PORT = "runnerPort";
    String RUNNER_IPV4_ADDRESS = "runnerIpv4Address";
    String MODULE_GROUPID = "moduleGroupId";
    String MODULE_ARTIFACTID = "moduleArtifactId";
    String MODULE_VERSION = "moduleVersion";
    String COMMIT_ID = "commitId";
    String USERNAME = "user";
    String PASSWORD = "pwd";
    String TEST_CLASS = "testClass";
    String RUN_NUMBER = "runNumber";
    String RUN_ID = "runId";
    String VCS_REPO_URL = "vcsRepoUrl";
    String TEST_PACKAGE = "testPackageBase";
    String MD5 = "md5";
}
