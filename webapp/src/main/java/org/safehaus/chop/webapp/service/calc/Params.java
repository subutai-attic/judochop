package org.safehaus.chop.webapp.service.calc;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Params {

    String moduleId;
    String testName;
    String commitId;
    int runNumber;
    String metricType;
    int percentile;
    String failureValue;

    public Params(String testName, String commitId, String metricType, int percentile, String failureValue) {
        this.testName = testName;
        this.commitId = commitId;
        this.metricType = metricType;
        this.percentile = percentile;
        this.failureValue = failureValue;
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

    public String getMetricType() {
        return metricType;
    }

    public int getPercentile() {
        return percentile;
    }

    public String getFailureValue() {
        return failureValue;
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
