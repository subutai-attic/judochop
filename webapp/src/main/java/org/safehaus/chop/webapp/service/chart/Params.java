package org.safehaus.chop.webapp.service.chart;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Params {

    private String moduleId;
    private String testName;
    private String commitId;
    private int runNumber;
    private String metricType;
    private int percentile = 100;
    private String failureValue;

    public Params(String testName, String commitId, String metricType, int percentile, String failureValue) {
        this.testName = testName;
        this.commitId = commitId;
        this.metricType = metricType;
        this.percentile = percentile;
        this.failureValue = failureValue;
    }

    public Params(String moduleId) {
        this.moduleId = moduleId;
    }

    public Params(String moduleId, String testName, String commitId, int runNumber, String metricType, int percentile, String failureValue) {
        this.moduleId = moduleId;
        this.testName = testName;
        this.commitId = commitId;
        this.runNumber = runNumber;
        this.metricType = metricType;
        this.percentile = percentile;
        this.failureValue = failureValue;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getTestName() {
        return testName;
    }

    public String getCommitId() {
        return commitId;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public String getMetricType() {
        return metricType;
    }

    public int getPercentile() {
        return percentile;
    }

    public String getFailureValue() {
        return failureValue;
    }

    public Params setModuleId(String moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public Params setCommitId(String commitId) {
        this.commitId = commitId;
        return this;
    }

    public Params setRunNumber(int runNumber) {
        this.runNumber = runNumber;
        return this;
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("moduleId", moduleId)
                .append("testName", testName)
                .append("commitId", commitId)
                .append("runNumber", runNumber)
                .append("metricType", metricType)
                .append("percentile", percentile)
                .append("failureValue", failureValue)
                .toString();
    }
}
