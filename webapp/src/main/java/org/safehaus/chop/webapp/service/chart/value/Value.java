package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.JSONObject;
import org.safehaus.chop.api.Run;
import org.safehaus.chop.api.RunResult;
import org.safehaus.chop.webapp.service.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Value {

    protected static Logger LOG = LoggerFactory.getLogger(Value.class);

    protected double value;
    protected long failures;
    protected long ignores;

    protected JSONObject properties = new JSONObject();

    public Value() {}

    public Value(RunResult runResult) {
        this.value = runResult.getRunTime();
        this.failures = runResult.getFailureCount();
        this.ignores = runResult.getIgnoreCount();
        JsonUtil.put(properties, "id", runResult.getId() );
    }

    public void merge(Value value) {}

    protected void calcValue(Run run) {}

    public void merge(Run run) {
        calcValue(run);
        inc(run.getFailures(), run.getIgnores());
        mergeProperties(run);
    }

    protected void inc(long failures, long ignores) {
        this.failures += failures;
        this.ignores += ignores;
    }

    private void mergeProperties(Run run) {
        JsonUtil.put(properties, "chopType", run.getChopType() );
        JsonUtil.put(properties, "commitId", run.getCommitId() );
        JsonUtil.put(properties, "runNumber", run.getRunNumber() );

        JsonUtil.inc(properties, "runners", 1);
        JsonUtil.inc(properties, "totalTestsRun", run.getTotalTestsRun() );
        JsonUtil.inc(properties, "iterations", run.getThreads() * run.getIterations() );
    }

    public double getValue() {
        return value;
    }

    public long getFailures() {
        return failures;
    }

    public long getIgnores() {
        return ignores;
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        JsonUtil.put(json, "value", getValue() );
        JsonUtil.put(json, "failures", failures);
        JsonUtil.put(json, "ignores", ignores);
        JsonUtil.copy(properties, json);

        return json;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("value", getValue() )
                .toString();
    }
}
