package org.safehaus.perftest.api.settings;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/8/13 Time: 5:29 PM To change this template use File | Settings |
 * File Templates.
 */
public interface ConfigKeys {
    String RUNNERS_PATH = "runners";
    String TESTS_PATH = "tests";

    String PERFTEST_VERSION_KEY = "perftest.version";

    String CREATE_TIMESTAMP_KEY = "create.timestamp";

    String GIT_UUID_KEY = "git.uuid";

    String GIT_URL_KEY = "git.url";

    String GROUP_ID_KEY = "group.id";

    String ARTIFACT_ID_KEY = "artifact.id";

    String TEST_MODULE_FQCN_KEY = "test.module.fqcn";

    String DEFAULT_TEST_MODULE = "org.safehaus.perftest.NoopPerftestModule";
}
