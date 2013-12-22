package org.safehaus.chop.server;


/**
 *
 */
public interface ConfigKeys extends org.safehaus.chop.api.ConfigKeys {
    String SERVER_PORT_KEY = "server.port";
    int DEFAULT_SERVER_PORT = 8080;

    String SERVER_INFO_KEY = "server.info";
    String CONTEXT_PATH = "context.path";

    String CONTEXT_TEMPDIR_KEY = "javax.servlet.context.tempdir";
    String MANAGER_ENDPOINT_KEY = "manager.endpoint";
    String DEFAULT_MANAGER_ENDPOINT = "http://localhost:8080/manager/text";
    String MANAGER_APP_PASSWORD_KEY = "manager.app.password";
    String MANAGER_APP_USERNAME_KEY = "manager.app.username";

    /** prop key for number of times to retry recovery operations */
    String RECOVERY_RETRY_COUNT_KEY = "recovery.retry.count";
    /** default for number of times to retry recovery operations */
    int DEFAULT_RECOVERY_RETRY_COUNT = 3;

    /** prop key for the time to wait between retry recovery operations */
    String DELAY_RETRY_KEY = "recovery.retry.delay";
    /** default for the time to wait in milliseconds between retry recovery operations */
    long DEFAULT_DELAY_RETRY = 10000;
}
