package org.safehaus.chop.server.drivers;


import org.safehaus.chop.api.StatsSnapshot;
import org.safehaus.chop.api.ISummary;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.State;


/**
 * A driver for a chop applied to a test class.
 */
public interface IDriver {
    void reset();

    StatsSnapshot getCallStatsSnapshot();

    State getState();

    ISummary getRun();

    boolean isRunning();

    boolean needsReset();

    long getStartTime();

    long getStopTime();

    void start();

    void stop();

    Project getProjectConfig();
}
