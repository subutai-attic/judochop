package org.safehaus.chop.api;


import java.util.Date;


/**
 * A specific commit of a Maven Module under test.
 */
public interface Commit {

    String getId();

    String getModuleId();

    String getMd5();

    String getRunnerPath();

    Date getCreateTime();
}
