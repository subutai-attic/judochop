package org.safehaus.perftest;


import org.safehaus.perftest.api.CallStatsSnapshot;
import org.safehaus.perftest.api.RunInfo;
import org.safehaus.perftest.api.State;
import org.safehaus.perftest.api.TestInfo;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/19/13 Time: 12:15 AM To change this template use File | Settings
 * | File Templates.
 */
public interface IController {
    void reset();

    CallStatsSnapshot getCallStatsSnapshot();

    State getState();

    RunInfo getRunInfo();

    boolean isRunning();

    boolean needsReset();

    long getStartTime();

    long getStopTime();

    void start();

    void stop();

    TestInfo getTestInfo();
}
