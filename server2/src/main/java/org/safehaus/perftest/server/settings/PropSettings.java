package org.safehaus.perftest.server.settings;


import com.netflix.config.DynamicPropertyFactory;


/** Easy access to dynamic properties. Make sure we do not cache values and get every time. */
public class PropSettings implements ConfigKeys {
    public static int getServerPort() {
        return DynamicPropertyFactory.getInstance().getIntProperty( SERVER_PORT_KEY, DEFAULT_SERVER_PORT ).get();
    }


    public static String getManagerEndpoint() {
        return DynamicPropertyFactory.getInstance().getStringProperty( MANAGER_ENDPOINT_KEY, DEFAULT_MANAGER_ENDPOINT )
                                     .get();
    }


    public static String getManagerAppUsername() {
        return DynamicPropertyFactory.getInstance().getStringProperty( MANAGER_APP_USERNAME_KEY, "admin" ).get();
    }


    public static String getManagerAppPassword() {
        return DynamicPropertyFactory.getInstance().getStringProperty( MANAGER_APP_PASSWORD_KEY, "secret" ).get();
    }


    public static int getRecoveryRetryCount() {
        return DynamicPropertyFactory.getInstance()
                                     .getIntProperty( RECOVERY_RETRY_COUNT_KEY, DEFAULT_RECOVERY_RETRY_COUNT ).get();
    }


    public static long getRecoveryRetryDelay() {
        return DynamicPropertyFactory.getInstance().getLongProperty( DELAY_RETRY_KEY, DEFAULT_DELAY_RETRY ).get();
    }


    public static long getSleepToStop() {
        return DynamicPropertyFactory.getInstance().getLongProperty( "sleep.to.stop", 100 ).get();
    }
}
