package org.safehaus.chop.api.settings;


/** Configuration setting keys. */
public interface ConfigKeys {

    String RUNNERS_PATH = "runners";

    String TESTS_PATH = "tests";

    String PERFTEST_VERSION_KEY = "perftest.version";

    String CREATE_TIMESTAMP_KEY = "create.timestamp";

    String GIT_UUID_KEY = "git.uuid";

    String GIT_URL_KEY = "git.url";

    String GROUP_ID_KEY = "group.id";

    String ARTIFACT_ID_KEY = "artifact.id";

    // @TODO - this will become the package base to use for finding annotated tests
    String TEST_MODULE_FQCN_KEY = "test.module.fqcn";

    String TEST_PACKAGE_BASE = "test.package.base";

    String TEST_STOP_TIMEOUT = "test.stop.timeout";

    // @TODO - this should go away
    String DEFAULT_TEST_MODULE = "org.safehaus.perftest.NoopPerftestModule";

    String LOAD_TIME_KEY = "load.time.key";

    String LOAD_KEY = "load.key";

    String IPV4_KEY = "public-ipv4";

    String HOSTNAME_KEY = "public-hostname";

    String SERVER_PORT_KEY = "server.port";

    String URL_KEY = "url.key";

    String RUNNER_TEMP_DIR_KEY = "runner.temp.dir";

    String PROJECT_VERSION_KEY = "project.version";

    String WAR_MD5_KEY = "war.md5";
}
