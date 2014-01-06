package org.safehaus.chop.api;


/** Constants ... */
public interface Constants {
    /** the project HTTP parameter */
    String PARAM_PROJECT = "project";

    /** the propagate HTTP parameter */
    String PARAM_PROPAGATE = "propagate";

    /** the default name to use for the runner's war file */
    String RUNNER_WAR = "runner.war";

    /** the number of characters of the UUID to use for UUID path component */
    int CHARS_OF_UUID = 8;

    /** the name of the project json file */
    String PROJECT_FILE = "project.json";

    /** the path to the runners */
    String RUNNERS_PATH = "runners";

    /** path to the tests */
    String CONFIGS_PATH = "tests";

    /** the suffix used for the run summary json file */
    String SUMMARY_SUFFIX = "-summary.json";

    /** the suffix used for the run results json file */
    String RESULTS_SUFFIX = "-results.json";
}
