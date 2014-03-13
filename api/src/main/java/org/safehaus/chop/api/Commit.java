package org.safehaus.chop.api;


import java.util.Date;
import java.util.UUID;


/**
 * A specific commit of a Maven Module under test.
 */
public interface Commit {

    String getId();

    String getCommitId();

    String getModuleId();

    String getWarMd5();

    Date getCreateTime();
}
