package org.safehaus.chop.runner;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


/**
 * Servlet configuration information.
 */
@FigSingleton
public interface CoordinatorFig extends GuicyFig {
    String UPLOAD_PATH = "coordinator.endpoint.upload";
    String UPLOAD_PATH_DEFAULT = "/upload";
    @Key( UPLOAD_PATH )
    @Default( UPLOAD_PATH_DEFAULT )
    String getUploadPath();


    String UPLOAD_RESULTS_PATH = "coordinator.endpoint.upload.results";
    String UPLOAD_RESULTS_PATH_DEFAULT = "/upload/results";
    @Key( UPLOAD_RESULTS_PATH )
    @Default( UPLOAD_RESULTS_PATH_DEFAULT )
    String getUploadResultsPath();


    String UPLOAD_SUMMARY_PATH = "coordinator.endpoint.upload.summary";
    String UPLOAD_SUMMARY_PATH_DEFAULT = "/upload/summary";
    @Key( UPLOAD_SUMMARY_PATH )
    @Default( UPLOAD_SUMMARY_PATH_DEFAULT )
    String getUploadSummaryPath();


    String RUN_STATUS_PATH = "coordinator.endpoint.run.status";
    String RUN_STATUS_PATH_DEFAULT = "/run/status";
    @Key( RUN_STATUS_PATH )
    @Default( RUN_STATUS_PATH_DEFAULT )
    String getRunStatusPath();


    String RUN_STATS_PATH = "coordinator.endpoint.run.stats";
    String RUN_STATS_PATH_DEFAULT = "/run/stats";
    @Key( RUN_STATS_PATH )
    @Default( RUN_STATS_PATH_DEFAULT )
    String getRunStatsPath();


    String RUNNERS_LIST_PATH = "coordinator.endpoint.runners.list";
    String RUNNERS_LIST_PATH_DEFAULT = "/runners/list";
    @Key( RUNNERS_LIST_PATH )
    @Default( RUNNERS_LIST_PATH_DEFAULT )
    String getRunnersListPath();


    String RUNNERS_REGISTER_PATH = "coordinator.endpoint.runners.register";
    String RUNNERS_REGISTER_PATH_DEFAULT = "/runners/register";
    @Key( RUNNERS_REGISTER_PATH )
    @Default( RUNNERS_REGISTER_PATH_DEFAULT )
    String getRunnersRegisterPath();


    String RUNNERS_UNREGISTER_PATH = "coordinator.endpoint.runners.unregister";
    String RUNNERS_UNREGISTER_PATH_DEFAULT = "/runners/unregister";
    @Key( RUNNERS_UNREGISTER_PATH )
    @Default( RUNNERS_UNREGISTER_PATH_DEFAULT )
    String getRunnersUnregisterPath();


    String ENDPOINT = "coordinator.endpoint";
    String ENDPOINT_DEFAULT = "http://localhost:8443";
    @Key( ENDPOINT )
    @Default( ENDPOINT_DEFAULT )
    String getEndpoint();


    String USERNAME = "coordinator.username";
    String USERNAME_DEFAULT = "testuser";
    @Key( USERNAME )
    @Default( USERNAME_DEFAULT )
    String getUsername();


    String PASSWORD = "coordinator.password";
    String PASSWORD_DEFAULT = "changeit";
    @Key( PASSWORD )
    @Default( PASSWORD_DEFAULT )
    String getPassword();
}
