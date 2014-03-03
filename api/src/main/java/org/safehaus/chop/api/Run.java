package org.safehaus.chop.api;


import java.util.Iterator;


/**
 * A run of a specific version of a Maven Module under test.
 */
public interface Run {

    Version getCommitVersion();

    Summary getSummary();

    Iterator<TestResult> getResults();

}
