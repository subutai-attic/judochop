package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class RunValue extends Value {

    private int count;

    protected int runNumber;

    protected int failures;
    protected int ignores;
    protected String chopType;
    protected String commitId;
    protected int runners;
    protected int totalTestsRun;
    protected int iterations;

    public void merge(Run run) {
        value += run.getAvgTime();
        count++;

        runNumber = run.getRunNumber();
    }

    @Override
    public double getValue() {
        return value / count;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append( "value", getValue() )
                .append("runNumber", runNumber)
                .toString();
    }

}
