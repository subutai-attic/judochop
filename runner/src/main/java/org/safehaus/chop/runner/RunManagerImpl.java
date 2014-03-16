package org.safehaus.chop.runner;


import java.io.File;

import org.safehaus.chop.api.*;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.spi.RunManager;

import org.apache.commons.lang.NotImplementedException;


/**
 * An implementation of the RunManager that works with the Coordinator service on the web ui.
 */
public class RunManagerImpl implements RunManager {

    @Override
    public void store( final Project project, final Summary summary, final File resultsFile,
                       final Class<?> testClass ) {
        throw new NotImplementedException();
    }


    @Override
    public boolean hasCompleted( final Runner runner, final Project project, final int runNumber,
                                 final Class<?> testClass ) {
        throw new NotImplementedException();
    }


    @Override
    public int getNextRunNumber( final Project project ) {
        throw new NotImplementedException();
    }
}
