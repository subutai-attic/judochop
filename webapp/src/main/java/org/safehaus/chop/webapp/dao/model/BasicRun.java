package org.safehaus.chop.webapp.dao.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.Summary;

public class BasicRun implements Run {

    private String id;
    private String commitId;
    private String runner;
    private int runNumber;
    private String testName;

    public BasicRun(String commitId, String runner, int runNumber, String testName) {
        id = createId(commitId, runner, runNumber, testName);
        this.commitId = commitId;
        this.runner = runner;
        this.runNumber = runNumber;
        this.testName = testName;
    }

    public String getId() {
        return id;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getRunner() {
        return runner;
    }

    public int getRunNumber() {
        return runNumber;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("commitId", commitId)
                .append("runner", runner)
                .append("runNumber", runNumber)
                .append("testName", testName)
                .toString();
    }

    private static String createId(String commitId, String runner, int runNumber, String testName) {
        return "" + new HashCodeBuilder()
                .append(commitId)
                .append(runner)
                .append(runNumber)
                .append(testName)
                .toHashCode();
    }
}
