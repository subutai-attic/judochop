package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.simple.JSONObject;
import org.safehaus.chop.api.RunResult;

public class AvgValue extends Value {

    private int count;

    public AvgValue() {
        super(0, 0, 0);
    }

    public void merge(Value value) {

        if (value == null) {
            return;
        }

        this.value += value.getValue();
        count++;

        failures += value.getFailures();
//        ignores += runValue.getIgnores();

//        properties.put( "commitId", run.getCommitId() );
//        properties.put( "runNumber", run.getRunNumber() );
        properties = value.getProperties();
    }

    public int getCount() {
        return count;
    }

    @Override
    public double getValue() {
        return value / count;
    }
}
