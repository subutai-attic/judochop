package org.safehaus.chop.api;


/** Configuration setting keys. */
public interface ConfigKeys {

    /** @TODO - this is a constant and not a key does not belong here */
    String PARAM_PROJECT = "project";

    /** @TODO - this is a constant and not a key does not belong here */
    String PARAM_PROPAGATE = "propagate";

    /** @TODO - this is a constant and not a key does not belong here */
    /** the default name to use for the runner's war file */
    String RUNNER_WAR = "runner.war";

    /** @TODO - this is a constant and not a key does not belong here */
    int CHARS_OF_UUID = 8;

    /** @TODO - this is a constant and not a key does not belong here */
    String PROJECT_FILE = "project.json";

    /** @TODO - this is a constant and not a key does not belong here */
    String RUNNERS_PATH = "runners";

    /** @TODO - this is a constant and not a key does not belong here */
    String CONFIGS_PATH = "tests";

    /** @TODO - this is a constant and not a key does not belong here */
    String SUMMARY_SUFFIX = "-summary.json";

    /** @TODO - this is a constant and not a key does not belong here */
    String RESULTS_SUFFIX = "-results.json";

    String CHOP_VERSION_KEY = "chop.version";

    String CREATE_TIMESTAMP_KEY = "create.timestamp";

    String GIT_UUID_KEY = "git.uuid";

    String GIT_URL_KEY = "git.url";

    String GROUP_ID_KEY = "group.id";

    String ARTIFACT_ID_KEY = "artifact.id";

    String TEST_PACKAGE_BASE = "test.package.base";

    String TEST_STOP_TIMEOUT = "test.stop.timeout";

    String DEFAULT_PACKAGE_BASE = "org.safehaus.chop";

    String LOAD_TIME_KEY = "load.time.key";

    /** Key used to locate the runner's war in the store. */
    String LOAD_KEY = "load.key";

    String IPV4_KEY = "public-ipv4";

    String HOSTNAME_KEY = "public-hostname";

    String SERVER_PORT_KEY = "server.port";

    String URL_KEY = "url.key";

    String RUNNER_TEMP_DIR_KEY = "runner.temp.dir";

    String PROJECT_VERSION_KEY = "project.version";

    String WAR_MD5_KEY = "war.md5";
}
