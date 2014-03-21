package org.safehaus.chop.webapp.service.chart.value;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.safehaus.chop.api.Run;

public class RunValue extends Value {

    private int count;

//    protected String chopType;
//    protected int runNumber;
//    protected long failures;
//    protected int ignores;
//    protected String commitId;
//    protected int runners;
//    protected int totalTestsRun;
//    protected int iterations;


    public RunValue() {
        super(0, 0, 0);
    }

    public void merge(Run run) {
        value += run.getAvgTime();
        count++;

        failures += run.getFailures();
        ignores += run.getIgnores();

        properties.put( "commitId", run.getCommitId() );
        properties.put( "runNumber", run.getRunNumber() );
    }

    public void merge(RunValue runValue) {
        value += runValue.getValue();
        count++;

        failures += runValue.getFailures();
//        ignores += runValue.getIgnores();

//        properties.put( "commitId", run.getCommitId() );
//        properties.put( "runNumber", run.getRunNumber() );
        properties = runValue.getProperties();
    }

    @Override
    public double getValue() {
        return value / count;
    }

//    @Override
//    public String toString() {
//        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
//                .append("value", getValue())
////                .append("runNumber", runNumber)
//                .toString();
//    }

}
