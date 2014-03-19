package org.safehaus.chop.webapp.view.chart;

public class Params {

    String testName;
    String commitId;
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
}
