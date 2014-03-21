package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class RunValue extends Value {

    private int count;

    protected int runNumber;

//    protected String chopType;
//    protected long failures;
//    protected int ignores;
//    protected String commitId;
//    protected int runners;
//    protected int totalTestsRun;
//    protected int iterations;

    public void merge(Run run) {
        value += run.getAvgTime();
        count++;

        runNumber = run.getRunNumber();
        failures += run.getFailures();
        ignores += run.getIgnores();

    }

    public void merge(RunValue runValue) {
        value += runValue.getValue();
        count++;

        failures += runValue.getFailures();
//        ignores += runValue.getIgnores();
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
