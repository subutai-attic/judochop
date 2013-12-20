package org.safehaus.chop.api.store.amazon;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/8/13 Time: 5:41 PM To change this template use File | Settings |
 * File Templates.
 */
public interface ConfigKeys extends org.safehaus.chop.api.settings.ConfigKeys {
    String AWSKEY_KEY = "aws.s3.key";
    String AWS_SECRET_KEY = "aws.s3.secret";
    String AWS_BUCKET_KEY = "aws.s3.bucket";
    String DEFAULT_BUCKET = "perftest-bucket";

    String SERVER_PORT_KEY = "server.port";
    String DEFAULT_SERVER_PORT = "8080";

    String RUNNER_TEMP_DIR = "runner.temp.dir";
    String DEFAULT_RUNNER_TEMP_DIR = "/tmp";

    String SCAN_PERIOD_KEY = "scan.period.milliseconds";
    long DEFAULT_SCAN_PERIOD = 300000L;
}
